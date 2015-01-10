/*
 * Copyright 2015 Peng Wan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.phylame.simbs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pw.phylame.ixin.frame.IFrame;
import pw.phylame.simbs.ds.Book;
import pw.phylame.simbs.ds.Customer;
import pw.phylame.simbs.ui.com.BookTablePane;
import pw.phylame.simbs.ui.com.CustomerTablePane;
import pw.phylame.simbs.ui.com.NavigatePane;
import pw.phylame.simbs.ui.com.PaneRender;
import pw.phylame.simbs.ui.dialog.*;
import pw.phylame.simbs.ui.MainFrame;
import pw.phylame.tools.sql.DbHelper;

import static pw.phylame.simbs.Constants.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * The UI and resource manager.
 */
public class Manager {
    private static Log log = LogFactory.getLog(Manager.class);

    public Manager(Application app) {
        this.app = app;
        worker = new Worker(app.getDbHelper());
        ui = new MainFrame(this);
        DialogFactory.setDialogParent(ui);
    }

    /** Start manager work loop */
    public void start() {
        ui.setTitle(app.getString("App.Title"));
        ui.setStatusText(app.getString("App.Ready"));
        ui.setVisible(true);

        viewHome();

        String[] args = app.getSystemArguments();
        if (args.length > 0) {
            onCommand(args[0]);
        }
    }

    /** Exit manager work loop */
    public void exit() {
        ui.setVisible(false);
        ui.dispose();
        worker.destroy();
        System.exit(0);
    }

    public IFrame getFrame() {
        return ui;
    }

    public void showAbout() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(String.format("%s v%s by %s %s", app.getString("App.Name"), APP_VERSION,
                app.getString("App.Author"), app.getString("App.Comment")));
        sb.append("<br/><br/>JRE: ").append(System.getProperty("java.runtime.version")).append(" ");
        sb.append(System.getProperty("os.arch")).append("<br/>");
        sb.append("JVM: ").append(System.getProperty("java.vm.name")).append(" by ");
        sb.append(System.getProperty("java.vm.vendor")).append("<br/>");
        sb.append("<br/>").append(app.getString("App.License")).append("<br/>");
        sb.append("<br/>").append(app.getString("App.Rights"));
        sb.append("</html>");
        JOptionPane.showMessageDialog(ui, sb.toString(), app.getString("Dialog.AboutApp.Title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void registerBook() {
        if (worker.newBook() != null) {
            BookTablePane tablePane = getBookTablePane();
            if (tablePane != null) {
                tablePane.reloadTable();
            }
        }
    }

    private void registerCustomer() {
        if (worker.newCustomer() != null) {
//            BookTablePane tablePane = getBookTablePane();
//            if (tablePane != null) {
//                tablePane.reloadTable();
//            }
        }
    }

    public void showStoreInfo() {
        StoreInfoDialog dialog = new StoreInfoDialog(getFrame(),
                app.getString("Dialog.Info.Title"));
        dialog.setVisible(true);
    }

    private void onViewDetails() {
        String isbn = getSelectedBook();
        if (isbn != null) {
            BookDetailsDialog dialog = new BookDetailsDialog(getFrame(),
                    app.getString("Dialog.BookDetails.Title"));
            dialog.setBook(isbn);
            dialog.setVisible(true);
            System.gc();
            return;
        }
        int id = getSelectedCustomer();
        if (id > 0) {
            CustomerDetailsDialog dialog = new CustomerDetailsDialog(getFrame(),
                    app.getString("Dialog.CustomerDetails.Title"));
            dialog.setCustomer(id);
            dialog.setVisible(true);
            System.gc();
        }
    }

    private void onModify() {
        BookTablePane tablePane = getBookTablePane();
        if (tablePane != null) {
            String isbn = tablePane.getSelectedBook();
            if (isbn == null) {
                return;
            }
            ModifyBookDialog dialog = new ModifyBookDialog(getFrame(),
                    app.getString("Dialog.ModifyBook.Title"));
            Book book = worker.getBook(isbn);
            dialog.setBook(book, false);
            dialog.setVisible(true);
            book = dialog.getBook();
            if (book != null) {
                try {
                    worker.updateBook(book);
                    tablePane.updateBook(tablePane.getSelectedRow(), book);
//                    tablePane.reloadTable();
                } catch (SQLException e) {
                    System.out.println(e.getSQLState());
                    log.debug("Cannot update book", e);
                }
            }
            System.gc();
            return;
        }

        int id = getSelectedCustomer();
        if (id != -1) {
            ModifyCustomerDialog dialog = new ModifyCustomerDialog(getFrame(),
                    app.getString("Dialog.ModifyCustomer.Title"));
            Customer customer = worker.getCustomer(id);
            dialog.setCustomer(customer);
            dialog.setVisible(true);
            customer = dialog.getCustomer();
            if (customer != null) {
                try {
                    worker.updateCustomer(customer);
                } catch (SQLException e) {
                    System.out.println(e.getSQLState());
                    log.debug("Cannot update customer", e);
                }
            }
            System.gc();
        }
    }

    private void onDelete() {
        BookTablePane tablePane = getBookTablePane();
        if (tablePane != null) {
            String[] books = tablePane.getSelectedBooks();
            if (books == null || books.length == 0) {
                return;
            }
            if (! DialogFactory.showConfirm(getFrame(), String.format(
                            app.getString("Dialog.DeleteBook.Tip"), books.length),
                    app.getString("Dialog.DeleteBook.Title"))) {
                return;
            }
            int deletedRows = 0;
            for (String isbn: books) {
                try {
                    worker.removeBook(isbn);
                    deletedRows++;
                } catch (SQLException e) {
                    if ("23504".equals(e.getSQLState())) {  // book is used
                        DialogFactory.showWarning(getFrame(),
                                String.format(app.getString("Dialog.DeleteBook.BookUsed"),
                                        worker.getBook(isbn).getName()),
                                app.getString("Dialog.DeleteBook.Title"));
                    } else {
                        log.debug("Cannot delete book", e);
                    }
                }
            }
            if (deletedRows > 0) {
                tablePane.reloadTable();
            }
            System.gc();
            return;
        }
    }

    private BookTablePane getBookTablePane() {
        if (paneRender == null || ! (paneRender instanceof BookTablePane)) {
            return null;
        }
        return (BookTablePane) paneRender;
    }

    private CustomerTablePane getCustomerTablePane() {
        if (paneRender == null || ! (paneRender instanceof CustomerTablePane)) {
            return null;
        }
        return (CustomerTablePane) paneRender;
    }

    private void viewBook() {
        if (paneRender != null) {
            paneRender.destroy();
            paneRender = null;
        }
        paneRender = new BookTablePane();
        ui.setContentArea(paneRender);
        System.gc();
    }

    private void viewCustomer() {
        if (paneRender != null) {
            paneRender.destroy();
            paneRender = null;
        }
        paneRender = new CustomerTablePane();
        ui.setContentArea(paneRender);
        System.gc();
    }

    private void viewHome() {
        if (paneRender != null) {
            paneRender.destroy();
            paneRender = null;
        }
        paneRender = new NavigatePane();
        ui.setContentArea(paneRender);
        System.gc();
    }

    /**
     * Get selected book if the book table is shown.
     * @return the ISBN of the book otherwise {@code null} if no table is shown
     */
    private String getSelectedBook() {
        BookTablePane tablePane = getBookTablePane();
        if (tablePane == null) {
            return null;
        } else {
            return tablePane.getSelectedBook();
        }
    }

    /**
     * Get selected customer if the book table is shown.
     * @return the ID of the book otherwise {@code -1} if no table is shown
     */
    private int getSelectedCustomer() {
        CustomerTablePane tablePane = getCustomerTablePane();
        if (tablePane == null) {
            return -1;
        } else {
            return tablePane.getSelectedCustomer();
        }
    }

    private void storeBook() {
        StoreBookDialog dialog = new StoreBookDialog(getFrame(), app.getString("Dialog.Store.Title"));
        dialog.setBook(getSelectedBook());
        dialog.setVisible(true);
        String isbn = dialog.getBook();
        int number = dialog.getNumber();
        BigDecimal total = dialog.getTotalPrice();
        String comm = dialog.getComment();
        System.gc();
        if (isbn == null || number <= 0) {
            return;
        }
        try {
            worker.storeBook(isbn, number, total, comm);
        } catch (SQLException e) {
            log.debug("Cannot save store-book record", e);
        }
    }

    private void sellBook() {
        SellBookDialog dialog = new SellBookDialog(getFrame(), app.getString("Dialog.Sell.Title"));
        dialog.setBook(getSelectedBook());
        dialog.setCustomer(getSelectedCustomer());
        dialog.setVisible(true);
        String isbn = dialog.getBook();
        int customerId = dialog.getCustomer();
        int sales = dialog.getNumber();
        BigDecimal price = dialog.getTotalPrice();
        String comm = dialog.getComment();
        System.gc();
        if (isbn == null || customerId <= 0 || sales <= 0) {
            return;
        }
        try {
            worker.sellBook(isbn, customerId, sales, price, comm);
        } catch (SQLException e) {
            log.debug("Cannot save sell-book record", e);
        }
    }

    private void lendBook() {
        LendBookDialog dialog = new LendBookDialog(getFrame(), app.getString("Dialog.Lend.Title"));
        dialog.setBook(getSelectedBook());
        dialog.setCustomer(getSelectedCustomer());
        dialog.setVisible(true);
        String isbn = dialog.getBook();
        int customerId = dialog.getCustomer();
        int number = dialog.getNumber(), period = dialog.getPeriod();
        BigDecimal deposit = dialog.getDeposit(), price = dialog.getPrice();
        String comm = dialog.getComment();
        System.gc();
        if (isbn == null || customerId <= 0 || number <= 0 || period <= 0) {
            return;
        }
        try {
            worker.lendBook(isbn, customerId, number, period, price, deposit, comm);
        } catch (SQLException e) {
            log.debug("Cannot save lend-book record", e);
        }
    }

    private void returnBook() {
        ReturnBookDialog dialog = new ReturnBookDialog(getFrame(),
                app.getString("Dialog.Return.Title"));
        dialog.setBook(getSelectedBook());
        dialog.setCustomer(getSelectedCustomer());
        dialog.setVisible(true);
        String isbn = dialog.getBook();
        int customerId = dialog.getCustomer(), number = dialog.getNumber();
        System.gc();
        if (isbn == null || customerId <= 0 || number <= 0) {
            return;
        }

    }

    public void onCommand(Object cmdId) {
        if (cmdId == null || ! (cmdId instanceof String)) {
            System.err.println("Invalid command");
            return;
        }
        switch ((String) cmdId) {
            case REGISTER_BOOK:
                registerBook();
                break;
            case REGISTER_CUSTOMER:
                registerCustomer();
                break;
            case STORE_PROPERTIES:
                showStoreInfo();
                break;
            case EDIT_VIEW:
                onViewDetails();
                break;
            case EDIT_MODIFY:
                onModify();
                break;
            case EDIT_DELETE:
                onDelete();
                break;
            case VIEW_BOOK:
                viewBook();
                break;
            case VIEW_CUSTOMER:
                viewCustomer();
                break;
            case VIEW_HOME:
                viewHome();
                break;
            case STORE_BOOK:
                storeBook();
                break;
            case SELL_BOOK:
                sellBook();
                break;
            case LEND_BOOK:
                lendBook();
                break;
            case RETURN_BOOK:
                returnBook();
                break;
            case EXIT_APP:
                exit();
                break;
            case HELP_ABOUT:
                showAbout();
                break;
            default:
                System.out.println(cmdId);
                break;
        }
    }

    /** The application */
    private Application app;

    /* The main frame */
    private MainFrame ui = null;

    /** The shown pane */
    private PaneRender paneRender = null;

    private Worker worker = null;
}

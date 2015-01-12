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

import pw.phylame.ixin.frame.IFrame;
import pw.phylame.simbs.ds.Book;
import pw.phylame.simbs.ds.Customer;
import pw.phylame.simbs.ui.com.*;
import pw.phylame.simbs.ui.dialog.*;
import pw.phylame.simbs.ui.MainFrame;

import static pw.phylame.simbs.Constants.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * The UI and resource manager.
 */
public class Manager {
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
        app.exit();
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
            updateBookPane();
        }
    }

    private void registerCustomer() {
        if (worker.newCustomer() != null) {
            updateCustomerPane();
        }
    }

    public void showStoreInfo() {
        StoreDetailsDialog.viewDetails(getFrame());
    }

    private void onViewDetails() {
        String isbn = getSelectedBook();
        if (isbn != null) {
            BookDetailsDialog.viewBook(getFrame(), isbn);
            System.gc();
            return;
        }
        int id = getSelectedCustomer();
        if (id > 0) {
            CustomerDetailsDialog.viewCustomer(getFrame(), id);
            System.gc();
            return;
        }
        int no = getSelectedBillRecord();
        if (no > 0) {
            BillDetailsDialog.viewBill(getFrame(), no);
            System.gc();
        }
    }

    /** Modify selected book */
    private void modifyBook() {
        BookTablePane tablePane = getBookTablePane();
        if (tablePane == null) {
            return;
        }
        String isbn = tablePane.getSelectedBook();
        if (isbn == null) {
            return;
        }
        Book book = worker.getBook(isbn);
        book = ModifyBookDialog.modifyBook(getFrame(), book, false);
        if (book != null) {
            try {
                worker.updateBook(book);
                tablePane.updateBook(tablePane.getSelectedRow(), book);
            } catch (SQLException e) {
                System.out.println(e.getSQLState());
                e.printStackTrace();
            }
        }
        System.gc();
    }

    /** Modify selected customer */
    private void modifyCustomer() {
        CustomerTablePane tablePane = getCustomerTablePane();
        if (tablePane == null) {
            return;
        }
        int id = tablePane.getSelectedCustomer();
        if (id <= 0) {
            return;
        }
        Customer customer = worker.getCustomer(id);
        customer = ModifyCustomerDialog.modifyCustomer(getFrame(), customer);
        if (customer != null) {
            try {
                worker.updateCustomer(customer);
                tablePane.updateCustomer(tablePane.getSelectedRow(), customer);
            } catch (SQLException e) {
                System.out.println(e.getSQLState());
                e.printStackTrace();
            }
        }
        System.gc();
    }

    private void onModify() {
        if (isBookShown()) {
            modifyBook();
        } else if (isCustomerShown()) {
            modifyCustomer();
        }
    }

    /** Delete selected books */
    private void deleteBooks() {
        BookTablePane tablePane = getBookTablePane();
        if (tablePane == null) {
            return;
        }
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
                    e.printStackTrace();
                }
            }
        }
        if (deletedRows > 0) {
            tablePane.reloadTable();
        }
        System.gc();
    }

    /** Delete selected customers */
    private void deleteCustomers() {
        CustomerTablePane tablePane = getCustomerTablePane();
        if (tablePane == null) {
            return;
        }
        int[] customers = tablePane.getSelectedCustomers();
        if (customers == null || customers.length ==0) {    // no choices
            return;
        }
        if (! DialogFactory.showConfirm(getFrame(), String.format(
                        app.getString("Dialog.DeleteCustomer.Tip"), customers.length),
                app.getString("Dialog.DeleteCustomer.Title"))) {
            return;
        }
        int deletedRows = 0;
        for (int id: customers) {
            try {
                worker.removeCustomer(id);
                deletedRows++;
            } catch (SQLException e) {
                if ("23504".equals(e.getSQLState())) {  // book is used
                    DialogFactory.showWarning(getFrame(),
                            String.format(app.getString("Dialog.DeleteCustomer.CustomerUsed"),
                                    worker.getCustomer(id).getName()),
                            app.getString("Dialog.DeleteCustomer.Title"));
                } else {
                    e.printStackTrace();
                }
            }
        }
        if (deletedRows > 0) {
            tablePane.reloadTable();
        }
        System.gc();
    }

    private void onDelete() {
        if (isBookShown()) {
            deleteBooks();
        } else if (isCustomerShown()) {
            deleteCustomers();
        }
    }

    private BookTablePane getBookTablePane() {
        if (! (paneRender instanceof BookTablePane)) {
            return null;
        }
        return (BookTablePane) paneRender;
    }

    private boolean isBookShown() {
        return paneRender instanceof BookTablePane;
    }

    private void viewBook() {
        if (isBookShown()) {
            return;
        }
        if (paneRender != null) {
            paneRender.destroy();
            paneRender = null;
        }
        paneRender = new BookTablePane();
        ui.setContentArea(paneRender);
        ((BookTablePane) paneRender).focusTable();
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

    private void updateBookPane() {
        BookTablePane tablePane = getBookTablePane();
        if (tablePane != null) {
            tablePane.reloadTable();
        }
    }

    private CustomerTablePane getCustomerTablePane() {
        if (! (paneRender instanceof CustomerTablePane)) {
            return null;
        }
        return (CustomerTablePane) paneRender;
    }

    private boolean isCustomerShown() {
        return paneRender instanceof CustomerTablePane;
    }

    private void viewCustomer() {
        if (isCustomerShown()) {
            return;
        }
        if (paneRender != null) {
            paneRender.destroy();
            paneRender = null;
        }
        paneRender = new CustomerTablePane();
        ui.setContentArea(paneRender);
        ((CustomerTablePane) paneRender).focusTable();
        System.gc();
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

    private void updateCustomerPane() {
        CustomerTablePane tablePane = getCustomerTablePane();
        if (tablePane != null) {
            tablePane.reloadTable();
        }
    }

    private boolean isHomeShown() {
        return paneRender instanceof NavigatePane;
    }

    private void viewHome() {
        if (isHomeShown()) {
            return;
        }
        if (paneRender != null) {
            paneRender.destroy();
            paneRender = null;
        }
        paneRender = new NavigatePane();
        ui.setContentArea(paneRender);
        System.gc();
    }

    private BillTablePane getBillTablePane() {
        if (! (paneRender instanceof BillTablePane)) {
            return null;
        }
        return (BillTablePane) paneRender;
    }

    private boolean isBillShown() {
        return paneRender instanceof BillTablePane;
    }

    private int getSelectedBillRecord() {
        BillTablePane tablePane = getBillTablePane();
        if (tablePane == null) {
            return -1;
        } else {
            return tablePane.getSelectedRecord();
        }
    }

    private void viewBill() {
        if (isBillShown()) {
            return;
        }
        if (paneRender != null) {
            paneRender.destroy();
            paneRender = null;
        }
        paneRender = new BillTablePane();
        ui.setContentArea(paneRender);
        ((BillTablePane) paneRender).focusTable();
        System.gc();
    }

    private void updateBillPane() {
        BillTablePane tablePane = getBillTablePane();
        if (tablePane != null) {
            tablePane.reloadTable();
        }
    }

    private void setPromote() {
        StartPromotionDialog dialog = new StartPromotionDialog(getFrame(),
                app.getString("Dialog.Promotion.Title"));
        dialog.setVisible(true);
        System.gc();
    }

    private void storeBook() {
        StoreBookDialog dialog = new StoreBookDialog(getFrame(), app.getString("Dialog.Store.Title"));
        dialog.setBook(getSelectedBook());
        dialog.setVisible(true);
        String isbn = dialog.getBook();
        int number = dialog.getNumber();
        BigDecimal total = dialog.getTotalPrice(), price = dialog.getPurchasePrice();
        String comm = dialog.getComment();
        System.gc();
        if (isbn == null || number <= 0) {
            return;
        }
        try {
            worker.storeBook(isbn, number, price, total, comm);
            BillTablePane tablePane = getBillTablePane();
            if (tablePane != null) {
                tablePane.reloadTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        if (isbn == null || sales <= 0) {
            return;
        }
        if (customerId < 0) {
            customerId = 0;     // anonymous customer
        }
        try {
            worker.sellBook(isbn, customerId, sales, price, comm);
            BillTablePane tablePane = getBillTablePane();
            if (tablePane != null) {
                tablePane.reloadTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rentBook() {
        RentBookDialog dialog = new RentBookDialog(getFrame(), app.getString("Dialog.Lend.Title"));
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
            worker.rentBook(isbn, customerId, number, period, price, deposit, comm);
            BillTablePane tablePane = getBillTablePane();
            if (tablePane != null) {
                tablePane.reloadTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            case EXIT_APP:
                exit();
                break;
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
            case VIEW_BILL:
                viewBill();
                break;
            case SET_PROMOTE:
                setPromote();
                break;
            case STORE_BOOK:
                storeBook();
                break;
            case SELL_BOOK:
                sellBook();
                break;
            case LEND_BOOK:
                rentBook();
                break;
            case RETURN_BOOK:
                returnBook();
                break;
            case UPDATE_BOOK_TABLE:
                updateBookPane();
                break;
            case UPDATE_CUSTOMER_TABLE:
                updateCustomerPane();
                break;
            case UPDATE_BILL_TABLE:
                updateBillPane();
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

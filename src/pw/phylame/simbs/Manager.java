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


import pw.phylame.simbs.ui.com.BookTablePane;
import pw.phylame.simbs.ui.com.NavigatePane;
import pw.phylame.simbs.ui.dialog.*;
import pw.phylame.simbs.ui.MainFrame;
import pw.phylame.tools.sql.SQLAdmin;

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
        worker = new Worker(app.getSQLAdmin());
        ui = new MainFrame(this);
        DialogFactory.setDialogParent(ui);
    }

    /** Start manager work loop */
    public void start() {
        ui.setTitle(app.getString("App.Title"));
        ui.setStatusText(app.getString("App.Ready"));
        ui.setVisible(true);

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

    public JFrame getUI() {
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
        worker.newBook();
    }

    private void registerCustomer() {
        worker.newCustomer();
    }

    public void showStoreInfo() {
        StoreInfoDialog dialog = new StoreInfoDialog();
        dialog.setIconImage(ui.getIconImage());
        dialog.setTitle(app.getString("Dialog.Info.Title"));
        dialog.setInfo(worker.getRegisteredBookNumber(), worker.getTotalInventories(), worker.getTotalSales(),
                worker.getLentNumber(), worker.getRegisteredCustomerCount());
        dialog.setVisible(true);
    }

    private void viewBook() {
        ui.setContentArea(new BookTablePane());
    }

    private void viewCustomer() {
        SQLAdmin sqlAdmin = app.getSQLAdmin();
    }

    private void storeBook() {
        StoreBookDialog dialog = new StoreBookDialog(app.getString("Dialog.Store.Title"));
        dialog.setVisible(true);
        String isbn = dialog.getISBN();
        int number = dialog.getNumber();
        if (isbn == null || number <= 0) {
            return;
        }
        try {
            worker.storeBook(isbn, number);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sellBook() {
        SellBookDialog dialog = new SellBookDialog(app.getString("Dialog.Sell.Title"));
        dialog.setVisible(true);
        String isbn = dialog.getISBN();
        int customerId = dialog.getCustomer();
        int sales = dialog.getSales();
        BigDecimal price = new BigDecimal(dialog.getTotalPrice());
        String comm = dialog.getComment();
        if (isbn == null || customerId <= 0 || sales <= 0) {
            return;
        }
        try {
            worker.sellBook(isbn, customerId, sales, price, comm);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void lendBook() {
        LendBookDialog dialog = new LendBookDialog(app.getString("Dialog.Lend.Title"));
        dialog.setVisible(true);
        String isbn = dialog.getISBN();
        int customerId = dialog.getCustomer();
        int number = dialog.getNumber(), period = dialog.getPeriod();
        BigDecimal total = new BigDecimal(dialog.getTotalPrice());
        String comm = dialog.getComment();
        if (isbn == null || customerId <= 0 || number <= 0 || period <= 0) {
            return;
        }
        try {
            worker.lendBook(isbn, customerId, number, period, total, comm);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void returnBook() {

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
            case VIEW_BOOK:
                viewBook();
                break;
            case VIEW_CUSTOMER:
                viewCustomer();
                break;
            case SHOW_NAVIGATE:
                ui.setContentArea(new NavigatePane());
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

    private Worker worker = null;
}

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


import pw.phylame.simbs.ui.dialog.DialogFactory;
import pw.phylame.simbs.ui.SimbsBoard;
import static pw.phylame.simbs.Constants.*;

import javax.swing.*;

/**
 * The UI and resource manager.
 */
public class Manager {
    public Manager(String[] args, SimbsApplication app) {
        this.args = args;
        this.app = app;
        worker = Worker.getInstance();
        ui = new SimbsBoard(this);
        DialogFactory.setDialogParent(ui);
    }

    /** Start manager work loop */
    public void start() {
        ui.setTitle(app.getString("App.Title"));
        ui.setStatusText(app.getString("App.Ready"));
        ui.setVisible(true);

        System.out.println("Book info: "+worker.getBookNumber()+" customer info: "+worker.getCustomerNumber());
        java.util.Map<String, Integer> res = worker.getExistingBooks();
        for (String isbn: res.keySet()) {
            System.out.printf("Book isbn=%s, number=%d\n", isbn, res.get(isbn));
        }

        System.out.println("Max customer ID: "+worker.getMaxCustomerId());
    }

    /** Exit manager work loop */
    public void exit() {
        ui.setVisible(false);
        ui.dispose();
        worker.destroy();
        System.exit(0);
    }

    public void showAbout() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(String.format("%s v%s by %s", app.getString("App.Name"), APP_VERSION,
                app.getString("App.Author")));
        sb.append("<br/> the <i>&quot;").append(app.getString("App.FullName")).append("&quot;</i>");
        sb.append("<br/>").append(app.getString("App.Rights"));
        sb.append("</html>");
        JOptionPane.showMessageDialog(ui, sb.toString(), app.getString("About.Title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void addBook() {

    }

    private void addCustomer() {

    }

    private void storeBook() {

    }

    private void sellBook() {

    }

    private void lendBook() {

    }

    private void returnBook() {

    }

    public void onCommand(Object cmdId) {
        if (cmdId == null || ! (cmdId instanceof String)) {
            System.err.println("Invalid command");
            return;
        }
        switch ((String) cmdId) {
            case FILE_EXIT:
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

    /* The system arguments */
    private String[] args = null;

    /** The application */
    private SimbsApplication app;

    /* The main frame */
    private SimbsBoard ui = null;

    private Worker worker = null;

}

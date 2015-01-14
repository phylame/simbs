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

package pw.phylame.simbs.ui.com;

import pw.phylame.simbs.Application;
import pw.phylame.simbs.Constants;
import pw.phylame.simbs.Worker;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by Peng Wan on 2015-1-14.
 */
public class PromotionDetailsPane extends PaneRender {
    private JPanel rootPane;
    private JTextField tfDateBegin;
    private JTextField tfDateEnd;
    private JTextField tfObject;
    private JFormattedTextField tfValue;
    private JTextField tfComment;
    private JFormattedTextField tfID;

    public PromotionDetailsPane(int id) {
        super();
        setID(id);
    }

    private void reset() {
        tfID.setText("");
        tfDateBegin.setText("");
        tfDateEnd.setText("");
        tfObject.setText("");
        tfValue.setText("");
        tfComment.setText("");
    }

    public void setID(int no) {
        if (no <= 0) {
            reset();
            return;
        }
        String sql = "SELECT Pobject, Pvalue, Pstart, Pend, Pcomment FROM promotion WHERE Pid=?";
        Application app = Application.getInstance();
        try {
            PreparedStatement ps = app.getDbHelper().prepareStatement(sql);
            ps.setInt(1, no);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfID.setValue(no);
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
                tfDateBegin.setText(sdf.format(rs.getDate(3)));
                tfDateEnd.setText(sdf.format(rs.getDate(4)));
                int obj = rs.getInt(1);
                String s = "";
                if (obj == Worker.PROMOTION_SALE) {
                    s = app.getString("Pane.PromotionDetails.ObjectSale");
                } else if (obj == Worker.PROMOTION_RENTAL) {
                    s = app.getString("Pane.PromotionDetails.ObjectRental");
                }
                tfObject.setText(s);
                tfValue.setValue(rs.getBigDecimal(2));
                tfComment.setText(Worker.normalizeString(rs.getString(5)));
            }
            rs.close();
            ps.close();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public JPanel getPane() {
        return rootPane;
    }
}

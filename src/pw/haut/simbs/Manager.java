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

package pw.haut.simbs;


import pw.haut.simbs.ui.SimbsBoard;

/**
 * The UI and resource manager.
 */
public class Manager {
    public Manager(String[] args) {
        this.args = args;
        ui = new SimbsBoard(this);
    }

    /** Start manager work loop */
    public void start() {
        ui.setVisible(true);
    }

    /** Exit manager work loop */
    public void exit() {
        ui.setVisible(false);
        ui.dispose();
        System.exit(0);
    }

    /* The system arguments */
    private String[] args = null;

    /* The main frame */
    private SimbsBoard ui = null;
}

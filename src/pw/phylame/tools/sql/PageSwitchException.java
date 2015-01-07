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

package pw.phylame.tools.sql;

/**
 * Exception when failed to switch page.
 */
public class PageSwitchException extends Exception {
    private int page;

    public PageSwitchException(int page, String reason) {
        super(reason);
        this.page = page;
    }

    public int getPageNumber() {
        return page;
    }
}

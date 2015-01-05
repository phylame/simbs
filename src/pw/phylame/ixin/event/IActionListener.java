/*
 * Copyright 2014 Peng Wan
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

package pw.phylame.ixin.event;

import java.util.EventListener;

/**
 * Listener for {@code IAction}.
 * Created by Peng Wan on 14-11-2.
 */
public interface IActionListener extends EventListener {
    void actionPerformed(IActionEvent e);
}

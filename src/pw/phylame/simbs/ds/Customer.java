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

package pw.phylame.simbs.ds;

import pw.phylame.simbs.Constants;

import java.util.Date;

/**
 * Customer descriptor.
 */
public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private int level;
    private int limit;
    private Date date;
    private String comment;

    public Customer() {
        this(1, "", "", "");
    }

    public Customer(int id, String name, String phone, String email) {
        this(id, name, phone, email, new Date(), 0, Constants.DEFAULT_LENT_LIMIT, "");
    }

    public Customer(int id, String name, String phone, String email, Date date, int level, int limit,
                    String comment) {
        setId(id);
        setName(name);
        setPhone(phone);
        setEmail(email);
        setDate(date);
        setLevel(level);
        setLimit(limit);
        setComment(comment);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("require customer ID >= 0");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return String.format("Customer <ID=%d, name=%s, phone=%s, email=%s, level=%d> @ 0x%X",
                getId(), getName(), getPhone(), getEmail(), getLevel(), hashCode());
    }
}

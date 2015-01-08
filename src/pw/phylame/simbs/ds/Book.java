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

import java.math.BigDecimal;
import java.util.Date;

/**
 * Book descriptor.
 */
public class Book {
    private String isbn;
    private String name;
    private String version;
    private String authors;
    private String cover;
    private Date date;
    private String category;
    private String publisher;
    private BigDecimal price;
    private String intro;

    public Book() {
        this("", "", "", "", "", new Date(), "", "", new BigDecimal(0.0D), "");
    }

    public Book(String isbn, String name, String version, String authors, String cover, Date date,
                String category, String publisher, BigDecimal price, String intro) {
        setISBN(isbn);
        setName(name);
        setVersion(version);
        setAuthors(authors);
        setCover(cover);
        setDate(date);
        setCategory(category);
        setPublisher(publisher);
        setPrice(price);
        setIntro(intro);
    }

    public String getISBN() {
        return isbn;
    }

    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAuthors() {
        return authors;
    }

    public String[] getAuthorList() {
        return getAuthors().split(",");
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    @Override
    public String toString() {
        return String.format("Book <ISBN=%s, name=%s> at 0x%X", getISBN(), getName(), hashCode());
    }
}

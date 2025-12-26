CREATE TABLE cart (
                      cart_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                      user_id BIGINT UNSIGNED NOT NULL,
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME NOT NULL,
                      PRIMARY KEY (cart_id),
                      UNIQUE KEY uk_cart_user (user_id),
                      CONSTRAINT fk_cart_user
                          FOREIGN KEY (user_id) REFERENCES `user`(user_id)
) ENGINE=InnoDB;

CREATE TABLE cart_item (
                           cart_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                           cart_id BIGINT UNSIGNED NOT NULL,
                           book_id BIGINT UNSIGNED NOT NULL,
                           quantity INT NOT NULL,
                           created_at DATETIME NOT NULL,
                           updated_at DATETIME NOT NULL,
                           PRIMARY KEY (cart_item_id),
                           UNIQUE KEY uk_cart_item_cart_book (cart_id, book_id),
                           CONSTRAINT fk_cart_item_cart
                               FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE,
                           CONSTRAINT fk_cart_item_book
                               FOREIGN KEY (book_id) REFERENCES book(book_id),
                           CONSTRAINT chk_cart_item_quantity CHECK (quantity >= 1)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS orders (
                                      order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                      user_id BIGINT UNSIGNED NOT NULL,
                                      status VARCHAR(30) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    canceled_at DATETIME NULL,
    PRIMARY KEY (order_id),
    CONSTRAINT fk_orders_user
    FOREIGN KEY (user_id) REFERENCES `user`(user_id)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_item (
                                          order_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                          order_id BIGINT UNSIGNED NOT NULL,
                                          book_id BIGINT UNSIGNED NOT NULL,
                                          title_snapshot VARCHAR(255) NOT NULL,
    price_snapshot DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (order_item_id),
    CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_book
    FOREIGN KEY (book_id) REFERENCES book(book_id),
    CONSTRAINT chk_order_item_quantity CHECK (quantity >= 1)
    ) ENGINE=InnoDB;

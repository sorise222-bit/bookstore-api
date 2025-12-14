-- Flyway Migration: V1__init_schema.sql
-- Target DB: MySQL (InnoDB)
-- NOTE:
-- - `user`는 MySQL 예약어 가능성이 있어 backtick(`)으로 감쌌습니다.
-- - FK 순서 문제 방지를 위해 부모 테이블부터 생성합니다.

-- =========================
-- 1) user
-- =========================
CREATE TABLE `user` (
                        user_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                        email VARCHAR(255) NOT NULL,
                        password_hash VARCHAR(255) NOT NULL,
                        name VARCHAR(100) NOT NULL,
                        role ENUM('user','admin') NOT NULL DEFAULT 'user',
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        deleted_at DATETIME NULL,
                        PRIMARY KEY (user_id),
                        UNIQUE KEY uk_user_email (email)
) ENGINE=InnoDB;

-- =========================
-- 2) book
-- =========================
CREATE TABLE book (
                      book_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                      title VARCHAR(255) NOT NULL,
                      description TEXT NULL,
                      price DECIMAL(10,2) NOT NULL,
                      stock INT NOT NULL,
                      authors TEXT NOT NULL,      -- JSON 문자열 배열 형태
                      categories TEXT NOT NULL,   -- JSON 문자열 배열 형태
                      published_at DATE NULL,
                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      PRIMARY KEY (book_id),
                      KEY idx_book_title (title),
                      KEY idx_book_published_at (published_at)
) ENGINE=InnoDB;

-- =========================
-- 3) refresh_token
-- =========================
CREATE TABLE refresh_token (
                               token_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                               user_id BIGINT UNSIGNED NOT NULL,
                               refresh_token VARCHAR(255) NOT NULL,
                               expires_at DATETIME NOT NULL,
                               revoked BOOLEAN NOT NULL DEFAULT 0,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (token_id),
                               UNIQUE KEY uk_refresh_token (refresh_token),
                               KEY idx_refresh_user (user_id),
                               CONSTRAINT fk_refresh_user
                                   FOREIGN KEY (user_id) REFERENCES `user`(user_id)
                                       ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- 4) review
-- =========================
CREATE TABLE review (
                        review_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                        user_id BIGINT UNSIGNED NOT NULL,
                        book_id BIGINT UNSIGNED NOT NULL,
                        rating INT NOT NULL,
                        content TEXT NOT NULL,
                        like_count INT NOT NULL DEFAULT 0,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        deleted_at DATETIME NULL,
                        PRIMARY KEY (review_id),
                        KEY idx_review_user (user_id),
                        KEY idx_review_book (book_id),
                        CONSTRAINT fk_review_user
                            FOREIGN KEY (user_id) REFERENCES `user`(user_id)
                                ON DELETE CASCADE ON UPDATE CASCADE,
                        CONSTRAINT fk_review_book
                            FOREIGN KEY (book_id) REFERENCES book(book_id)
                                ON DELETE CASCADE ON UPDATE CASCADE,
                        CONSTRAINT chk_review_rating CHECK (rating >= 1 AND rating <= 5)
) ENGINE=InnoDB;

-- =========================
-- 5) comment
-- =========================
CREATE TABLE comment (
                         comment_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                         review_id BIGINT UNSIGNED NOT NULL,
                         user_id BIGINT UNSIGNED NOT NULL,
                         content TEXT NOT NULL,
                         like_count INT NOT NULL DEFAULT 0,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         deleted_at DATETIME NULL,
                         PRIMARY KEY (comment_id),
                         KEY idx_comment_review (review_id),
                         KEY idx_comment_user (user_id),
                         CONSTRAINT fk_comment_review
                             FOREIGN KEY (review_id) REFERENCES review(review_id)
                                 ON DELETE CASCADE ON UPDATE CASCADE,
                         CONSTRAINT fk_comment_user
                             FOREIGN KEY (user_id) REFERENCES `user`(user_id)
                                 ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- 6) review_like
-- =========================
CREATE TABLE review_like (
                             review_like_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                             review_id BIGINT UNSIGNED NOT NULL,
                             user_id BIGINT UNSIGNED NOT NULL,
                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (review_like_id),
                             UNIQUE KEY uk_review_like (review_id, user_id),
                             KEY idx_review_like_review (review_id),
                             KEY idx_review_like_user (user_id),
                             CONSTRAINT fk_review_like_review
                                 FOREIGN KEY (review_id) REFERENCES review(review_id)
                                     ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT fk_review_like_user
                                 FOREIGN KEY (user_id) REFERENCES `user`(user_id)
                                     ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- 7) comment_like
-- =========================
CREATE TABLE comment_like (
                              comment_like_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                              comment_id BIGINT UNSIGNED NOT NULL,
                              user_id BIGINT UNSIGNED NOT NULL,
                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (comment_like_id),
                              UNIQUE KEY uk_comment_like (comment_id, user_id),
                              KEY idx_comment_like_comment (comment_id),
                              KEY idx_comment_like_user (user_id),
                              CONSTRAINT fk_comment_like_comment
                                  FOREIGN KEY (comment_id) REFERENCES comment(comment_id)
                                      ON DELETE CASCADE ON UPDATE CASCADE,
                              CONSTRAINT fk_comment_like_user
                                  FOREIGN KEY (user_id) REFERENCES `user`(user_id)
                                      ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- 8) wishlist
-- =========================
CREATE TABLE wishlist (
                          wishlist_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                          user_id BIGINT UNSIGNED NOT NULL,
                          book_id BIGINT UNSIGNED NOT NULL,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (wishlist_id),
                          UNIQUE KEY uk_wishlist (user_id, book_id),
                          KEY idx_wishlist_user (user_id),
                          KEY idx_wishlist_book (book_id),
                          CONSTRAINT fk_wishlist_user
                              FOREIGN KEY (user_id) REFERENCES `user`(user_id)
                                  ON DELETE CASCADE ON UPDATE CASCADE,
                          CONSTRAINT fk_wishlist_book
                              FOREIGN KEY (book_id) REFERENCES book(book_id)
                                  ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- 9) cart_item
-- =========================
CREATE TABLE cart_item (
                           cart_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                           user_id BIGINT UNSIGNED NOT NULL,
                           book_id BIGINT UNSIGNED NOT NULL,
                           quantity INT NOT NULL,
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (cart_item_id),
                           UNIQUE KEY uk_cart_item (user_id, book_id),
                           KEY idx_cart_user (user_id),
                           KEY idx_cart_book (book_id),
                           CONSTRAINT fk_cart_user
                               FOREIGN KEY (user_id) REFERENCES `user`(user_id)
                                   ON DELETE CASCADE ON UPDATE CASCADE,
                           CONSTRAINT fk_cart_book
                               FOREIGN KEY (book_id) REFERENCES book(book_id)
                                   ON DELETE CASCADE ON UPDATE CASCADE,
                           CONSTRAINT chk_cart_quantity CHECK (quantity > 0)
) ENGINE=InnoDB;

-- =========================
-- 10) orders
-- =========================
CREATE TABLE orders (
                        order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                        user_id BIGINT UNSIGNED NOT NULL,
                        status ENUM('pending','paid','shipped','completed','canceled') NOT NULL DEFAULT 'pending',
                        total_price DECIMAL(10,2) NOT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        canceled_at DATETIME NULL,
                        PRIMARY KEY (order_id),
                        KEY idx_orders_user (user_id),
                        KEY idx_orders_status (status),
                        CONSTRAINT fk_orders_user
                            FOREIGN KEY (user_id) REFERENCES `user`(user_id)
                                ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- 11) order_item
-- =========================
CREATE TABLE order_item (
                            order_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                            order_id BIGINT UNSIGNED NOT NULL,
                            book_id BIGINT UNSIGNED NOT NULL,
                            unit_price DECIMAL(10,2) NOT NULL,
                            quantity INT NOT NULL,
                            subtotal DECIMAL(10,2) NOT NULL,
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (order_item_id),
                            KEY idx_order_item_order (order_id),
                            KEY idx_order_item_book (book_id),
                            CONSTRAINT fk_order_item_order
                                FOREIGN KEY (order_id) REFERENCES orders(order_id)
                                    ON DELETE CASCADE ON UPDATE CASCADE,
                            CONSTRAINT fk_order_item_book
                                FOREIGN KEY (book_id) REFERENCES book(book_id)
                                    ON DELETE RESTRICT ON UPDATE CASCADE,
                            CONSTRAINT chk_order_item_quantity CHECK (quantity > 0)
) ENGINE=InnoDB;

-- =========================
-- 12) user_book_activity
-- =========================
CREATE TABLE user_book_activity (
                                    activity_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                    user_id BIGINT UNSIGNED NOT NULL,
                                    book_id BIGINT UNSIGNED NOT NULL,
                                    activity_type VARCHAR(30) NOT NULL, -- 'view', 'wishlist', 'cart', 'purchase', 'review' 등
                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (activity_id),
                                    KEY idx_activity_user (user_id),
                                    KEY idx_activity_book (book_id),
                                    KEY idx_activity_type (activity_type),
                                    CONSTRAINT fk_activity_user
                                        FOREIGN KEY (user_id) REFERENCES `user`(user_id)
                                            ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT fk_activity_book
                                        FOREIGN KEY (book_id) REFERENCES book(book_id)
                                            ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

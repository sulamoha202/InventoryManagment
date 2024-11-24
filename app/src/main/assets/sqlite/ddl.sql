CREATE TABLE IF NOT EXISTS store_info (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT,
    phone TEXT,
    email TEXT
);
INSERT INTO store_info (name,address,phone,email) VALUES
("TecnoStore", "Calle Madrid N14", "987-654-321", "cliente@TecnoStore.com");


CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    name TEXT NOT NUll,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    store_id INTEGER NOT NULL,
    FOREIGN KEY (store_id) REFERENCES store_info(id) ON DELETE SET NULL
);

INSERT INTO users(username,password,name,email,store_id) VALUES
 ("moha","test123","Mohamed","sula@test.es",1),
 ("admin","admin","Admin","admin@test.es",1);


CREATE TABLE IF NOT EXISTS revenue_table (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date TEXT NOT NULL,
    revenue REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    quantity INTEGER DEFAULT 0,
    price REAL DEFAULT 0.0,
    category TEXT,
    description TEXT,
    imageReference TEXT
);


CREATE TABLE IF NOT EXISTS inventory_movements (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL,
    movement_type TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    date TEXT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

INSERT INTO revenue_table(date, revenue) VALUES
("2024-11-01", 500.0),
("2024-11-02", 450.0),
("2024-11-03", 700.0),
("2024-11-04", 600.0),
("2024-11-05", 800.0),
("2024-11-06", 550.0),
("2024-11-07", 620.0),
("2024-11-08", 480.0),
("2024-11-09", 750.0),
("2024-11-10", 610.0),
("2024-11-11", 670.0),
("2024-11-12", 530.0),
("2024-11-13", 700.0),
("2024-11-14", 690.0),
("2024-11-15", 720.0),
("2024-11-16", 810.0),
("2024-11-17", 620.0),
("2024-11-18", 590.0),
("2024-11-19", 650.0),
("2024-11-20", 740.0),
("2024-11-21", 560.0),
("2024-11-22", 800.0),
("2024-11-23", 670.0),
("2024-11-24", 590.0),
("2024-11-25", 780.0),
("2024-11-26", 620.0),
("2024-11-27", 720.0),
("2024-11-28", 810.0),
("2024-11-29", 530.0),
("2024-11-30", 860.0);

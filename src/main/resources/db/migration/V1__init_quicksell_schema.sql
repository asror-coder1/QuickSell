create table if not exists users (
    id uuid primary key,
    full_name varchar(255) not null,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    role varchar(255) not null check (role in ('ADMIN', 'SELLER')),
    balance numeric(19,2) not null default 0,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table if not exists shops (
    id uuid primary key,
    seller_id uuid not null references users(id),
    bot_token varchar(255) not null,
    bot_username varchar(255) not null unique,
    domain_name varchar(255) unique,
    theme_color varchar(255),
    active boolean not null default true,
    expires_at timestamp with time zone not null,
    plan varchar(255) not null check (plan in ('FREE', 'PRO', 'BUSINESS')),
    status varchar(255) not null check (status in ('ACTIVE', 'DISABLED', 'EXPIRED')),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table if not exists categories (
    id uuid primary key,
    shop_id uuid not null references shops(id),
    name varchar(255) not null,
    icon_url varchar(255),
    sort_order integer not null default 0,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table if not exists products (
    id uuid primary key,
    shop_id uuid not null references shops(id),
    category_id uuid references categories(id),
    name varchar(255) not null,
    description text,
    price numeric(19,2) not null,
    discount_price numeric(19,2),
    stock_quantity integer not null default 0,
    images jsonb,
    available boolean not null default true,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table if not exists orders (
    id uuid primary key,
    shop_id uuid not null references shops(id),
    customer_tg_id bigint not null,
    customer_phone varchar(255) not null,
    total_amount numeric(19,2) not null,
    status varchar(255) not null check (status in ('NEW', 'PROCESSING', 'PAID', 'CANCELLED')),
    payment_type varchar(255) not null check (payment_type in ('CASH', 'CARD')),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table if not exists payments (
    id uuid primary key,
    order_id uuid not null references orders(id),
    transaction_id varchar(255) not null unique,
    provider varchar(255) not null check (provider in ('CLICK', 'PAYME')),
    amount numeric(19,2) not null,
    status varchar(255) not null check (status in ('PENDING', 'SUCCESS', 'FAILED')),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table if not exists order_items (
    id uuid primary key,
    order_id uuid not null references orders(id),
    product_id uuid not null references products(id),
    quantity integer not null,
    unit_price numeric(19,2) not null
);

create index if not exists idx_categories_shop_id on categories(shop_id);
create index if not exists idx_products_shop_id on products(shop_id);
create index if not exists idx_products_category_id on products(category_id);
create index if not exists idx_orders_shop_id on orders(shop_id);
create index if not exists idx_payments_order_id on payments(order_id);
create index if not exists idx_shops_seller_id on shops(seller_id);

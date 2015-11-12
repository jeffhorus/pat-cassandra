# IF4031-CassandraTwitter
Simple Twitter-Like Terminal Application using Java and Cassandra.

## Created By:
- Christ Angga Saputra - 13512019
- Jeffrey Lingga Binangkit - 13512059

## Petunjuk instalasi/building
1. Ada 2 cara untuk instalasi program ini, yaitu:
  - Clone dari `https://github.com/jeffhorus/pat-cassandra`
  - Ekstrak source code zip

2. Selanjutnya `Import Project` dengan menggunakan IntelliJ IDEA

## Cara menjalankan program
1. Buka project
2. Klik kanan pada MainClass.java, klik `Run MainClass.Main()`

## Perintah/query database untuk mengimplementasikan fungsionalitas

### Mendaftar user baru
1. Cek apakah telah ada username yang ingin didaftarkan dengan perintah `SELECT * FROM users WHERE username = '<username>'`
2. Jika belum ada, daftarkan username dan password ke dalam database dengan perintah `INSERT INTO users (username, password) VALUES ('<username>', '<password>')`

## Login
1. Cek apakah username dan password sesuai dengan database dengan perintah `SELECT * FROM users WHERE username = '<username>' AND password = '<password>'`
2. Jika ada, maka user berhasil login

### Follow a friend
1. Cek apakah username teman sama dengan username sendiri
2. Jika berbeda, cek apakah terdapat username teman tersebut dengan perintah `SELECT * FROM users WHERE username = '<username>'`
3. Jika terdapat teman dengan username tersebut, maka masukkan username teman ke dalam database dengan perintah `INSERT INTO friends (username, friend, since) VALUES ('<username_self>', '<username_friend>', 'now')` dan perintah `INSERT INTO followers (username, follower, since) VALUES ('<username_friend>', '<username_self>', 'now')`

### Tweet
1. Masukkan tweet ke dalam tweets dengan perintah `INSERT INTO tweets (tweet_id, username, body) VALUES (<tweet_id>, '<username>', '<body>')`
2. Masukkan tweet ke dalam userline agar mudah dalam menampilkan tweet per user dengan perintah `INSERT INTO userline (username, time, tweet_id) VALUES ('<username>', <time>, <tweet_id>)`
3. Masukkan tweet ke dalam timeline agar mudah dalam menampilkan timeline per user dengan perintah `INSERT INTO timeline (username, time, tweet_id) VALUES ('<username>', <time>, <tweet_id>)`

### Menampilkan tweet per user
1. Ambil semua tweet_id dari userline dengan perintah `SELECT * FROM userline WHERE username = '<username>'`
2. Kemudian untuk setiap tweet_id, ambil tweet tersebut dengan perintah `SELECT * FROM tweets WHERE tweet_id = <tweet_id>`

### Menampilkan timeline per user
1. Ambil semua tweet_id dari timeline dengan perintah `SELECT * FROM timeline WHERE username = '<username>'`
2. Kemudian untuk setiap tweet_id, ambil tweet tersebut dengan perintah `SELECT * FROM tweets WHERE tweet_id = <tweet_id>`

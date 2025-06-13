# 📚 Library Management System
An advanced Library Management System developed using **Spring Boot**, **Thymeleaf**, and **Oracle Database**. This application streamlines library operations by providing functionalities such as user registration, book reservation, borrowing and return processes, fine management, and inventory control.

---

## 👥 Project Members

- Muhammad Ausaja Hussain (22K-5186)
- Ammar Hyder (22K-4816)

---

## 🚀 Features
- **User Registration & Authentication**: Secure sign-up and login functionalities for users.
- **Book Reservation**: Allows users to reserve books online.
- **Borrowing & Returning**: Manages the borrowing and returning process efficiently.
- **Fine Management**: Calculates and manages fines for overdue books.
- **Inventory Management**: Keeps track of book inventory, including available and issued books.
- **Admin Dashboard**: Provides administrators with tools to manage users, books, and transactions.

---

## 🛠️ Technologies Used
- **Backend**: Java, Spring Boot
- **Frontend**: Thymeleaf, HTML, CSS
- **Database**: Oracle Database
- **Build Tool**: Maven

---

## 📂 Project Structure
Library-Management-System/  
├── src/  
│ ├── main/  
│ │ ├── java/  
│ │ │ └── com/  
│ │ │ └── example/  
│ │ │ └── library/  
│ │ │ ├── controllers/  
│ │ │ ├── models/  
│ │ │ └── services/  
│ │ └── resources/  
│ │ ├── templates/  
│ │ └── application.properties  
├── pom.xml  
└── README.md  

---

## ⚙️ Installation & Setup

1. **Clone the repository**:
```
git clone https://github.com/MuhammadAusajaHussain/Library-Management-System.git
```
   
2. **Navigate to the project directory:**
```
cd Library-Management-System
```

3. **Configure the Oracle Database:**
  * Ensure Oracle Database is installed and running.
  * Create a new schema/user for the application.
  * Update the application.properties file with your database credentials:
      ```
      spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      ```

4. **Build the project using Maven:**
```
mvn clean install
```

5. **Run the application:**
```
mvn spring-boot:run
```

6. **Access the application:**
Open your browser and go to: http://localhost:8080/

---

## 📸 Screenshots

1. **Login Page:**
![image](https://github.com/user-attachments/assets/05478688-a37b-4e27-b26c-9718d71a6ae0)

2. **Register Page**
![image](https://github.com/user-attachments/assets/dba17b08-994e-400c-970e-70c7f1ab1d9a)

3. **Admin Home Page**
![image](https://github.com/user-attachments/assets/452203a3-83a4-4be4-9388-b41b2676f506)

4. **Manage Users**
![image](https://github.com/user-attachments/assets/ead49cae-b0a8-4f71-8951-0a96b73a450b)

5. **Manage Books**
![image](https://github.com/user-attachments/assets/46a9bf86-b6fa-4361-aabd-86cb182c53df)

6. **View/Edit Books**
![image](https://github.com/user-attachments/assets/c5f257ca-0997-42bc-a91b-c3a24aee46b6)

7. **Edit User**
![image](https://github.com/user-attachments/assets/a78b9006-3220-4aa4-930d-f7a16e9ad1ec)

8. **View Reservation**
![image](https://github.com/user-attachments/assets/23acaa57-558d-4574-9815-4c75e82e5cb7)

9. **OverDue**
![image](https://github.com/user-attachments/assets/3f2efa3a-75e7-4766-882e-21d7210288a1)

10. **Manage Fine**
![image](https://github.com/user-attachments/assets/e0e49354-db2e-4a0f-adbf-e36951bce045)

11. **User Home**
![image](https://github.com/user-attachments/assets/33361bfb-1229-40ba-aacb-18f1e588bc0e)

12. **View Profile**
![image](https://github.com/user-attachments/assets/749abc04-859f-41b7-9294-0498d136a876)

13. **Edit Profile**
![image](https://github.com/user-attachments/assets/23e63879-d048-48fd-9781-1bea62e07e05)

14. **Borrow Books**
![image](https://github.com/user-attachments/assets/6f08726c-95ab-4a69-9c1a-27399e59cf75)

15. **Reserved Books**
![image](https://github.com/user-attachments/assets/dce2b9b6-e920-4340-8956-f1e119e24ad0)

16. **Reserve Books**
![image](https://github.com/user-attachments/assets/dab6fb5c-3553-45b6-8cff-a777283ad1da)

17. **Return Books**
![image](https://github.com/user-attachments/assets/d77f173d-3c56-428e-a701-f6bf6e0d5efc)

18. **Check/Pay Fine**
![image](https://github.com/user-attachments/assets/ba92c513-73b7-4bc6-8951-32931b55bb7b)

---

## 🤝 Contributing
Contributions are welcome! If you'd like to enhance the project, please fork the repository and submit a pull request.

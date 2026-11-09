# HỆ THỐNG QUẢN LÝ THƯ VIỆN SỐ

## Cấu trúc thư mục
```text
DigitalLibraryManagement/
├── src/
│   ├── MainApp.java                     # Khởi chạy giao diện chính
│   ├── controller/
│   │   └── LibraryController.java       # Điều phối MVC
│   ├── entity/                          # Các lớp thực thể dữ liệu
│   │   ├── IDocument.java
│   │   ├── EBook.java
│   │   ├── AudioBook.java
│   │   ├── ScientificJournal.java
│   │   ├── Reader.java
│   │   └── LoanRecord.java
│   ├── repository/                      # Tầng truy xuất CSDL
│   │   ├── IDataRepository.java
│   │   ├── InMemoryRepository.java
│   │   └── SqlJdbcRepository.java
│   ├── patterns/                        # 6 Mẫu thiết kế (comment giải thích trong code)
│   │   ├── singleton/
│   │   ├── factory/
│   │   ├── decorator/
│   │   ├── facade/
│   │   ├── strategy/
│   │   └── observer/
│   ├── view/                            # Tầng giao diện tinh giản
│   │   ├── ILibraryView.java
│   │   ├── SwingLibraryView.java
│   │   └── ConsoleLibraryView.java
│   └── test/
│       └── TestRunner.java              # Chạy kiểm thử ở Console
├── run_app.bat                          # Chạy giao diện đồ họa
└── run_test.bat                         # Chạy test case trên console
```

## Cách chạy trên VS Code
1. Mở thư mục `DigitalLibraryManagement` trong VS Code.
2. Mở `src/MainApp.java` và nhấn **Run** (hoặc `F5`) để mở giao diện **Thư viện số**.
3. Để chạy Test Case trên Console: Mở `src/test/TestRunner.java` và nhấn **Run**.

document.addEventListener("DOMContentLoaded", function () {
    fetchCategories().then(function() {
        fetchBooks();
        fetchUsers();
        populateCategoryDropdown();
    });
    document.getElementById("btnAddBook").addEventListener("click", addBook);
    document.getElementById("btnAddUser").addEventListener("click", addUser);
    document.getElementById("btnAddCategory").addEventListener("click", addCategory);
});

let categories = [];

function fetchBooks() {
    return fetch("/api/products")
        .then(function(r) { return r.json(); })
        .then(function(data) { displayBooks(data); })
        .catch(function(e) { console.error("Loi lay san pham:", e); });
}

function displayBooks(books) {
    var bookList = document.getElementById("bookList");
    bookList.innerHTML = "";
    books.forEach(function(book) {
        var category = categories.find(function(cat) { return cat.id === book.categoryId; });
        var categoryName = category ? category.name : "Khong co danh muc";
        var priceFormatted = book.price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
        var row = "<tr>" +
            "<td>" + book.id + "</td>" +
            "<td>" + book.name + "</td>" +
            "<td>" + priceFormatted + "</td>" +
            "<td>" + book.description + "</td>" +
            "<td>" + categoryName + "</td>" +
            "<td>" +
                '<button class="btn btn-warning" onclick="editBook(' + book.id + ', \'' + book.name + '\', ' + book.price + ', \'' + book.description + '\', ' + (book.categoryId || '') + ')">Sửa</button> ' +
                '<button class="btn btn-danger" onclick="deleteBook(' + book.id + ')">Xoá</button>' +
            "</td>" +
        "</tr>";
        bookList.innerHTML += row;
    });
}

function addBook() {
    var id = document.getElementById("bookId").value;
    var name = document.getElementById("bookName").value;
    var price = parseFloat(document.getElementById("bookPrice").value);
    var description = document.getElementById("bookDescription").value;
    var categoryId = document.getElementById("bookCategory").value;

    var body = {
        name: name,
        price: price,
        description: description,
        categoryId: categoryId ? parseInt(categoryId) : null
    };

    var url = id ? "/api/products/" + id : "/api/products";
    var method = id ? "PUT" : "POST";

    fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    })
    .then(function(r) { return r.json(); })
    .then(function() {
        fetchBooks();
        resetBookForm();
    })
    .catch(function(e) { console.error("Loi them/sua san pham:", e); });
}

function deleteBook(id) {
    if (!confirm("Ban co chac muon xoa san pham nay?")) return;
    fetch("/api/products/" + id, { method: "DELETE" })
        .then(function() { fetchBooks(); })
        .catch(function(e) { console.error("Loi xoa san pham:", e); });
}

function editBook(id, name, price, description, categoryId) {
    document.getElementById("bookId").value = id;
    document.getElementById("bookName").value = name;
    document.getElementById("bookPrice").value = price;
    document.getElementById("bookDescription").value = description;
    document.getElementById("bookCategory").value = categoryId || "";
}

function resetBookForm() {
    document.getElementById("bookId").value = "";
    document.getElementById("bookName").value = "";
    document.getElementById("bookPrice").value = "";
    document.getElementById("bookDescription").value = "";
    document.getElementById("bookCategory").value = "";
}

function deleteAllBooks() {
    if (!confirm("Ban co chac muon xoa tat ca san pham?")) return;
    fetch("/api/products", { method: "DELETE" })
        .then(function() { fetchBooks(); })
        .catch(function(e) { console.error("Loi xoa tat ca san pham:", e); });
}

function populateCategoryDropdown() {
    var dropdown = document.getElementById("bookCategory");
    dropdown.innerHTML = '<option value="">Chon danh muc</option>';
    categories.forEach(function(cat) {
        dropdown.innerHTML += '<option value="' + cat.id + '">' + cat.name + '</option>';
    });
}

function fetchUsers() {
    return fetch("/api/users")
        .then(function(r) { return r.json(); })
        .then(function(data) { displayUsers(data); })
        .catch(function(e) { console.error("Loi lay nguoi dung:", e); });
}

function displayUsers(users) {
    var userList = document.getElementById("userList");
    userList.innerHTML = "";
    users.forEach(function(user) {
        var row = "<tr>" +
            "<td>" + user.id + "</td>" +
            "<td>" + user.name + "</td>" +
            "<td>" + user.email + "</td>" +
            "<td>" + user.role + "</td>" +
            "<td>" + (user.dateOfBirth || "") + "</td>" +
            "<td>" + (user.address || "") + "</td>" +
            "<td>" + (user.phoneNumber || "") + "</td>" +
            "<td>" + (user.createdAt || "") + "</td>" +
            "<td>" +
                '<button class="btn btn-warning" onclick="editUser(' + user.id + ', \'' + user.name + '\', \'' + user.email + '\', \'' + user.role + '\', \'' + (user.dateOfBirth || '') + '\', \'' + (user.address || '') + '\', \'' + (user.phoneNumber || '') + '\')">Sửa</button> ' +
                '<button class="btn btn-danger" onclick="deleteUser(' + user.id + ')">Xoá</button>' +
            "</td>" +
        "</tr>";
        userList.innerHTML += row;
    });
}

function addUser() {
    var id = document.getElementById("userId").value;
    var name = document.getElementById("userName").value;
    var email = document.getElementById("userEmail").value;
    var password = document.getElementById("userPassword").value;
    var role = document.getElementById("userRole").value;
    var dateOfBirth = document.getElementById("userDateOfBirth").value;
    var address = document.getElementById("userAddress").value;
    var phoneNumber = document.getElementById("userPhoneNumber").value;

    var body = {
        name: name,
        email: email,
        role: role,
        dateOfBirth: dateOfBirth || null,
        address: address || null,
        phoneNumber: phoneNumber || null
    };

    if (!id) {
        body.password = password;
    } else if (password && password !== "defaultPassword") {
        body.password = password;
    }

    var url = id ? "/api/users/" + id : "/api/users";
    var method = id ? "PUT" : "POST";

    fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    })
    .then(function(r) { return r.json(); })
    .then(function() {
        fetchUsers();
        resetUserForm();
    })
    .catch(function(e) { console.error("Loi them/sua nguoi dung:", e); });
}

function deleteUser(id) {
    if (!confirm("Ban co chac muon xoa nguoi dung nay?")) return;
    fetch("/api/users/" + id, { method: "DELETE" })
        .then(function() { fetchUsers(); })
        .catch(function(e) { console.error("Loi xoa nguoi dung:", e); });
}

function editUser(id, name, email, role, dateOfBirth, address, phoneNumber) {
    document.getElementById("userId").value = id;
    document.getElementById("userName").value = name;
    document.getElementById("userEmail").value = email;
    document.getElementById("userPassword").value = "defaultPassword";
    document.getElementById("userRole").value = role;
    document.getElementById("userDateOfBirth").value = dateOfBirth || "";
    document.getElementById("userAddress").value = address || "";
    document.getElementById("userPhoneNumber").value = phoneNumber || "";
}

function resetUserForm() {
    document.getElementById("userId").value = "";
    document.getElementById("userName").value = "";
    document.getElementById("userEmail").value = "";
    document.getElementById("userPassword").value = "";
    document.getElementById("userRole").value = "USER";
    document.getElementById("userDateOfBirth").value = "";
    document.getElementById("userAddress").value = "";
    document.getElementById("userPhoneNumber").value = "";
}

function deleteAllUsers() {
    if (!confirm("Ban co chac muon xoa tat ca nguoi dung?")) return;
    fetch("/api/users", { method: "DELETE" })
        .then(function() { fetchUsers(); })
        .catch(function(e) { console.error("Loi xoa tat ca nguoi dung:", e); });
}

function fetchCategories() {
    return fetch("/api/categories")
        .then(function(r) { return r.json(); })
        .then(function(data) {
            categories = data;
            displayCategories(data);
        })
        .catch(function(e) { console.error("Loi lay danh muc:", e); });
}

function displayCategories(cats) {
    var categoryList = document.getElementById("categoryList");
    categoryList.innerHTML = "";
    cats.forEach(function(cat) {
        var row = "<tr>" +
            "<td>" + cat.id + "</td>" +
            "<td>" + cat.name + "</td>" +
            "<td>" + (cat.description || "") + "</td>" +
            "<td>" +
                '<button class="btn btn-warning" onclick="editCategory(' + cat.id + ', \'' + cat.name + '\', \'' + (cat.description || '') + '\')">Sửa</button> ' +
                '<button class="btn btn-danger" onclick="deleteCategory(' + cat.id + ')">Xoá</button>' +
            "</td>" +
        "</tr>";
        categoryList.innerHTML += row;
    });
}

function addCategory() {
    var id = document.getElementById("categoryId").value;
    var name = document.getElementById("categoryName").value;
    var description = document.getElementById("categoryDescription").value;

    var body = {
        name: name,
        description: description
    };

    var url = id ? "/api/categories/" + id : "/api/categories";
    var method = id ? "PUT" : "POST";

    fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    })
    .then(function(r) { return r.json(); })
    .then(function() {
        fetchCategories().then(function() {
            populateCategoryDropdown();
        });
        resetCategoryForm();
    })
    .catch(function(e) { console.error("Loi them/sua danh muc:", e); });
}

function deleteCategory(id) {
    if (!confirm("Ban co chac muon xoa danh muc nay?")) return;
    fetch("/api/categories/" + id, { method: "DELETE" })
        .then(function() {
            fetchCategories().then(function() {
                populateCategoryDropdown();
            });
        })
        .catch(function(e) { console.error("Loi xoa danh muc:", e); });
}

function editCategory(id, name, description) {
    document.getElementById("categoryId").value = id;
    document.getElementById("categoryName").value = name;
    document.getElementById("categoryDescription").value = description || "";
}

function resetCategoryForm() {
    document.getElementById("categoryId").value = "";
    document.getElementById("categoryName").value = "";
    document.getElementById("categoryDescription").value = "";
}

function deleteAllCategories() {
    if (!confirm("Ban co chac muon xoa tat ca danh muc?")) return;
    fetch("/api/categories", { method: "DELETE" })
        .then(function() {
            fetchCategories().then(function() {
                populateCategoryDropdown();
            });
        })
        .catch(function(e) { console.error("Loi xoa tat ca danh muc:", e); });
}

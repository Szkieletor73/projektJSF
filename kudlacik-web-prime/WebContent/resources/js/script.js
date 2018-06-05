function validateForm() {
    var x = document.forms["login"]["email"].value;
    if (x == "") {
        alert("Email must be filled out");
        return false;
    }
}
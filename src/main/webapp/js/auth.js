document.addEventListener("DOMContentLoaded", () => {

    const userName = localStorage.getItem("username");
    const loginLink = document.getElementById("loginLink");

    if (!loginLink) return;

    if (userName && userName !== "null" && userName.trim() !== "") {

        loginLink.outerHTML = `
        <div class="dropdown">
            <a class="nav-link dropdown-toggle"
               href="#"
               role="button"
               data-bs-toggle="dropdown">
               <i class="fa-solid fa-user"></i>
               ${userName}
            </a>

            <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="profile.html">My Profile</a></li>
                <li><a class="dropdown-item" href="my-donations.html">My Donations</a></li>
                <li><a class="dropdown-item text-danger" href="#" onclick="logoutUser()">Logout</a></li>
            </ul>
        </div>
        `;
    }
});

function logoutUser() {
    localStorage.removeItem("loggedInUser");
    localStorage.removeItem("username");

    sessionStorage.clear();

    window.location.href = "index.html";
}
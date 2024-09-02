document.addEventListener("DOMContentLoaded", function () {
  fetch("navbar.html")
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok " + response.statusText);
      }
      return response.text();
    })
    .then((data) => {
      document.getElementById("navbar-container").innerHTML = data;
      updateNavbar();
    })
    .catch((error) => {
      console.error(
        "There has been a problem with your fetch operation:",
        error
      );
    });
});

function updateNavbar() {
  const token = localStorage.getItem("authToken");
  const navbar = document.querySelector("#navbar-container");

  // Hide or show the Upload button based on token presence
  const uploadNavItem = navbar.querySelector('a[href="upload.html"]');
  if (token) {
    uploadNavItem.style.display = "block"; // Show upload button if logged in
  } else {
    uploadNavItem.style.display = "none"; // Hide upload button if not logged in
  }

  // Create the logout button
  const logoutButton = document.createElement("li");
  logoutButton.className = "nav-item";
  logoutButton.innerHTML = `
    <a class="nav-link" href="#" onclick="logout()">Logout</a>
  `;

  // Update navbar based on token presence
  if (token) {
    const navbarNavMlAuto = navbar.querySelector(".navbar-nav.ml-auto");
    if (navbarNavMlAuto) {
      navbarNavMlAuto.appendChild(logoutButton);

      // Hide sign-in and register links
      navbar
        .querySelectorAll(".navbar-nav.ml-auto .nav-item")
        .forEach((item) => {
          const link = item.querySelector("a");
          if (
            link &&
            (link.href.includes("login.html") ||
              link.href.includes("register.html"))
          ) {
            item.style.display = "none";
          }
        });
    }
  } else {
    // Show sign-in and register links if not logged in
    navbar.querySelectorAll(".navbar-nav.ml-auto .nav-item").forEach((item) => {
      const link = item.querySelector("a");
      if (
        link &&
        (link.href.includes("login.html") ||
          link.href.includes("register.html"))
      ) {
        item.style.display = "";
      }
    });
  }
}

function logout() {
  localStorage.removeItem("authToken");
  window.location.href = "index.html";
}

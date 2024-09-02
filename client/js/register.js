document.addEventListener("DOMContentLoaded", () => {
  const registerForm = document.getElementById("registerForm");

  registerForm.addEventListener("submit", async (event) => {
    event.preventDefault(); // Prevent default form submission

    // Gather form data
    const formData = {
      firstName: document.getElementById("firstName").value,
      lastName: document.getElementById("lastName").value,
      email: document.getElementById("email").value,
      password: document.getElementById("password").value,
    };

    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/auth/register",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        }
      );

      // Handle the response
      if (response.ok) {
        const data = await response.json();
        // Assuming the backend returns a token
        if (data.token) {
          // Set the token in localStorage or cookies if necessary
          localStorage.setItem("authToken", data.token);
          // Redirect to login or home page
          window.location.href = "login.html";
        } else {
          console.error("Token not found in the response.");
        }
      } else {
        console.error("Registration failed:", response.statusText);
      }
    } catch (error) {
      console.error("Error during registration:", error);
    }
  });
});

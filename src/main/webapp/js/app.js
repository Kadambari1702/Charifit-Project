// Charifit General JavaScript Logic
const BASE_URL = "";

document.addEventListener("DOMContentLoaded", () => {
	
	const loginLink = document.getElementById("loginLink");

	if (loginLink) {
	    const username = localStorage.getItem("username");

	    console.log("DEBUG username:", username);
	    console.log("DEBUG loginLink:", loginLink);

	    if (username && username !== "null" && username.trim() !== "") {
	        loginLink.innerText = "👤 " + username;
	        loginLink.href = "#";
	    } else {
	        loginLink.innerText = "Login/Register";
	        loginLink.href = "user-login.html";
	    }
		
	}
		
	const loggedInUser = localStorage.getItem("loggedInUser");
	
	
	
	const userName = localStorage.getItem("loggedInUser");

	const userDropdown = document.getElementById("userDropdown");

	if (userDropdown && userName) {
	    userDropdown.textContent = userName;
	}
	function logoutUser() {
		    localStorage.removeItem("loggedInUser");
		    window.location.href = "index.html";
		}
	
    // 1. Navigation effect on scroll
    const navbar = document.querySelector(".navbar-charifit");
    if (navbar) {
        window.addEventListener("scroll", () => {
            if (window.scrollY > 50) {
                navbar.classList.add("scrolled");
            } else {
                navbar.classList.remove("scrolled");
            }
        });
    }
	const logoutBtn = document.getElementById("logoutBtn");

	console.log("Logout button:", logoutBtn);

	if (logoutBtn) {
	    logoutBtn.addEventListener("click", function (e) {
	        e.preventDefault();

	        console.log("Logout clicked");

	        localStorage.clear();

	        window.location.href = "index.html";
	    });
	}

    // 2. Animate Counter Stats on Home Page
    animateCounters();

    // 3. Load Causes (if on index.html or causes.html)
    const causesContainer = document.getElementById("causes-container");
    if (causesContainer) {
        const isLimited = causesContainer.dataset.limit === "3";
        loadCauses(causesContainer, isLimited);
    }

    // 4. Handle Volunteer Registration
    const volunteerForm = document.getElementById("volunteerForm");
    if (volunteerForm) {
        volunteerForm.addEventListener("submit", handleVolunteerSubmit);
    }

    // 5. Handle Contact Form Query
    const contactForm = document.getElementById("contactForm");
    if (contactForm) {
        contactForm.addEventListener("submit", handleContactSubmit);
    }
});



// Animate impact counters
function animateCounters() {
    const counters = document.querySelectorAll(".counter-value");
    if (counters.length === 0) return;

    // Fetch dynamic stats for counters from backend first, if available
fetch(`${BASE_URL}/api/donations?action=stats`)
        .then(response => {
            if (response.ok) return response.json();
            throw new Error();
        })
        .then(stats => {
            const raisedCounter = document.getElementById("stat-raised");
            const donationsCounter = document.getElementById("stat-donations");

            if (raisedCounter && stats.totalRaised) {
                raisedCounter.setAttribute("data-target", Math.round(stats.totalRaised));
                raisedCounter.innerText = "Rs" + Math.round(stats.totalRaised);
            }
            if (donationsCounter && stats.totalCount) {
                donationsCounter.setAttribute("data-target", stats.totalCount);
                donationsCounter.innerText = stats.totalCount;
            }
            runCounterAnimation();
        })
        .catch(() => {
            // Fallback to defaults set in HTML
            runCounterAnimation();
        });
}

function runCounterAnimation() {
    const counters = document.querySelectorAll(".counter-value");
    counters.forEach(counter => {
        const updateCount = () => {
            const text = counter.getAttribute("data-target");
            const target = +text;
            const currentText = counter.innerText.replace(/[^0-9]/g, '');
            const count = +currentText;

            // Speed relative to target
            const speed = target / 100 > 1 ? target / 100 : 1;

            if (count < target) {
                const newCount = Math.min(target, Math.ceil(count + speed));
                if (counter.id === "stat-raised" || counter.getAttribute("data-prefix") === "Rs") {
                    counter.innerText = "Rs" + newCount.toLocaleString();
                } else {
                    counter.innerText = newCount.toLocaleString() + (counter.getAttribute("data-suffix") || "");
                }
                setTimeout(updateCount, 15);
            } else {
                if (counter.id === "stat-raised" || counter.getAttribute("data-prefix") === "Rs") {
                    counter.innerText = "Rs" + target.toLocaleString();
                } else {
                    counter.innerText = target.toLocaleString() + (counter.getAttribute("data-suffix") || "");
                }
            }
        };
        updateCount();
    });
}

// Fetch and render causes dynamically
function loadCauses(container, isLimited) {
    container.innerHTML = `
        <div class="col-12 text-center my-5">
            <div class="spinner-border text-teal" role="status">
                <span class="visually-hidden">Loading causes...</span>
            </div>
        </div>
    `;

fetch(`${BASE_URL}/api/causes`)
        .then(response => response.json())
        .then(causes => {
            container.innerHTML = "";
            if (causes.length === 0) {
                container.innerHTML = `<div class="col-12 text-center"><p class="text-muted">No active charity causes at the moment.</p></div>`;
                return;
            }

            // If limited (e.g. homepage), only show top 3
            const list = isLimited ? causes.slice(0, 3) : causes;

            list.forEach(cause => {
                const percent = Math.min(100, Math.round((cause.raisedAmount / cause.goalAmount) * 100));
                
                const cardCol = document.createElement("div");
                cardCol.className = isLimited ? "col-lg-4 col-md-6 mb-4" : "col-lg-4 col-md-6 mb-4";
                
                cardCol.innerHTML = `
                    <div class="cause-card">
                        <div class="cause-img-wrapper">
                            <img src="${cause.imageUrl || 'https://images.unsplash.com/photo-1593113598332-cd288d649433?auto=format&fit=crop&q=80&w=600'}" alt="${cause.title}">
                            <span class="cause-tag">${cause.status}</span>
                        </div>
                        <div class="cause-content">
                            <h4 class="cause-title">${cause.title}</h4>
                            <p class="cause-desc">${cause.description.length > 120 ? cause.description.substring(0, 120) + '...' : cause.description}</p>
                            <div class="progress-wrapper">
                                <div class="progress">
                                    <div class="progress-bar" role="progressbar" style="width: ${percent}%" aria-valuenow="${percent}" aria-valuemin="0" aria-valuemax="100"></div>
                                </div>
                                <div class="progress-info">
                                    <span>Raised: $${cause.raisedAmount.toLocaleString()}</span>
                                    <span>Goal: $${cause.goalAmount.toLocaleString()}</span>
                                    <span class="text-teal">${percent}%</span>
                                </div>
                            </div>
                            <a href="donate.html?causeId=${cause.id}" class="btn btn-teal w-100 mt-auto">Donate Now</a>
                        </div>
                    </div>
                `;
                container.appendChild(cardCol);
            });
        })
        .catch(err => {
            console.error("Error loading causes:", err);
            container.innerHTML = `
                <div class="col-12 text-center my-4">
                    <p class="text-danger">Failed to load causes from database. Check server connection.</p>
                </div>
            `;
        });
}

// Submit volunteer details via AJAX
function handleVolunteerSubmit(e) {
    e.preventDefault();
    const submitBtn = e.target.querySelector("button[type='submit']");
    const originalText = submitBtn.innerText;
    submitBtn.disabled = true;
    submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...`;

    const volunteerData = {
        name: document.getElementById("volName").value,
        email: document.getElementById("volEmail").value,
        phone: document.getElementById("volPhone").value,
        message: document.getElementById("volMessage").value
    };

fetch(`${BASE_URL}/api/volunteers`, {
		        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(volunteerData)
    })
    .then(response => response.json())
    .then(data => {
        submitBtn.disabled = false;
        submitBtn.innerText = originalText;

        if (data.success) {
            alertSuccess(e.target, data.message);
            volunteerForm.reset();
        } else {
            alertError(e.target, data.message);
        }
    })
    .catch(err => {
        submitBtn.disabled = false;
        submitBtn.innerText = originalText;
        alertError(e.target, "Connection failed. Make sure server is running.");
    });
}

// Submit contact queries via AJAX
function handleContactSubmit(e) {
    e.preventDefault();
    const submitBtn = e.target.querySelector("button[type='submit']");
    const originalText = submitBtn.innerText;
    submitBtn.disabled = true;
    submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Sending...`;

    const contactData = {
        name: document.getElementById("conName").value,
        email: document.getElementById("conEmail").value,
        subject: document.getElementById("conSubject").value,
        message: document.getElementById("conMessage").value
    };

fetch(`${BASE_URL}/api/contacts`, {
	        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(contactData)
    })
    .then(response => response.json())
    .then(data => {
        submitBtn.disabled = false;
        submitBtn.innerText = originalText;

        if (data.success) {
            alertSuccess(e.target, data.message);
            contactForm.reset();
        } else {
            alertError(e.target, data.message);
        }
    })
    .catch(err => {
        submitBtn.disabled = false;
        submitBtn.innerText = originalText;
        alertError(e.target, "Connection failed. Make sure server is running.");
    });
}

// Helper alert display functions
function alertSuccess(form, message) {
    removeAlerts(form);
    const alertDiv = document.createElement("div");
    alertDiv.className = "alert alert-success alert-dismissible fade show mt-3";
    alertDiv.innerHTML = `
        <strong>Success!</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    form.appendChild(alertDiv);
}

function alertError(form, message) {
    removeAlerts(form);
    const alertDiv = document.createElement("div");
    alertDiv.className = "alert alert-danger alert-dismissible fade show mt-3";
    alertDiv.innerHTML = `
        <strong>Error:</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    form.appendChild(alertDiv);
}

function removeAlerts(form) {
    const existingAlerts = form.querySelectorAll(".alert");
    existingAlerts.forEach(alert => alert.remove());
}

function formatDate(obj) {
    if (!obj) return "-";

    const date =
        obj.createdAt ||
        obj.created_at ||
        obj.date ||
        obj.timestamp;

    if (!date) return "-";

    return new Date(date).toLocaleString();
}



// ===============================
// Charifit Admin Dashboard (FINAL CLEAN)
// ===============================

const BASE_URL = "";



document.addEventListener("DOMContentLoaded", () => {

    checkAuthStatus();
    setupSidebarNavigation();

    const causeForm = document.getElementById("causeForm");
    if (causeForm) causeForm.addEventListener("submit", handleCauseSubmit);

    const logoutBtn = document.getElementById("adminLogoutBtn");
    if (logoutBtn) logoutBtn.addEventListener("click", handleLogout);

    // ✅ FIX: define loginLink properly
    const loginLink = document.getElementById("loginLink");
    const username = localStorage.getItem("username");

    console.log("DEBUG username:", username);
    console.log("DEBUG loginLink:", loginLink);

    if (!loginLink) return; // IMPORTANT SAFETY CHECK

    if (username && username.trim() !== "" && username !== "null") {
        loginLink.innerText = "👤 " + username;
        loginLink.href = "#";
    } else {
        loginLink.innerText = "Login/Register";
        loginLink.href = "user-login.html";
    }
});




// ===============================
// AUTH
// ===============================
function checkAuthStatus() {
    fetch(`${BASE_URL}/api/auth/status`)
        .then(r => r.json())
        .then(data => {
            if (data.authenticated) {
                document.getElementById("adminNameDisplay").innerText =
                    data.user?.fullName || "Admin";

                loadDashboardData();
            } else {
                window.location.href = "login.html";
            }
        })
        .catch(() => window.location.href = "login.html");
}


// ===============================
// NAV
// ===============================
function setupSidebarNavigation() {

    document.querySelectorAll(".admin-nav-item").forEach(item => {

        item.addEventListener("click", (e) => {
            e.preventDefault();

            const target = item.dataset.target;

            document.querySelectorAll(".admin-nav-item")
                .forEach(n => n.classList.remove("active"));
            item.classList.add("active");

            document.querySelectorAll(".admin-section")
                .forEach(s => s.classList.toggle("d-none", s.id !== target));

            if (target === "dashboard-section") loadDashboardData();
            if (target === "causes-section") loadCausesList();
            if (target === "donations-section") loadDonationsList();
            if (target === "volunteers-section") loadVolunteersList();
            if (target === "queries-section") loadQueriesList();
        });

    });
}


// ===============================
// DASHBOARD
// ===============================
function loadDashboardData() {

    fetch(`${BASE_URL}/api/donations`)
        .then(r => r.json())
        .then(d => {

            document.getElementById("card-donations-count").innerText = d.length;

            let totalRaised = 0;

            d.forEach(item => {
                totalRaised += Number(item.amount || 0);
            });

            document.getElementById("card-total-raised").innerText =
                "Rs" + totalRaised;
        });

    fetch(`${BASE_URL}/api/causes`)
        .then(r => r.json())
        .then(c => {
            document.getElementById("card-causes-count").innerText = c.length;
        });

    fetch(`${BASE_URL}/api/volunteers`)
        .then(r => r.json())
        .then(v => {
            document.getElementById("card-volunteers-count").innerText = v.length;
        });

    fetch(`${BASE_URL}/api/contacts`)
        .then(r => r.json())
        .then(c => {
            document.getElementById("card-queries-count").innerText = c.length;
        });

    loadCausesList();
    loadDonationsList();
    loadVolunteersList();
    loadQueriesList();
}

// ===============================
// SAFE TEXT
// ===============================
function safeText(t) {
    if (!t) return "-";
    return String(t)
        .replace(/â|â€/g, '"')
        .replace(/â|â€/g, '"')
        .replace(/â/g, "'")
        .replace(/â€“/g, "-");
}


// ===============================
// DATE
// ===============================
function formatDate(o) {
    if (!o) return "-";

    return (
        o.donationDate ||
        o.createdAt ||
        o.date ||
        o.timestamp ||
        "-"
    );
}


// ===============================
// CAUSES
// ===============================
function loadCausesList() {

    const tbody = document.getElementById("causesTableBody");

    if (!tbody) {
        console.error("causesTableBody not found in HTML");
        return;
    }

    fetch(`${BASE_URL}/api/causes`)
        .then(r => r.json())
        .then(data => {

            tbody.innerHTML = "";

			data.forEach(c => {
			    tbody.innerHTML += `
			        <tr>
			            <td>${c.id}</td>
			            <td>${c.title}</td>
			            <td>${c.goalAmount}</td>
			            <td>${c.raisedAmount}</td>
			            <td>${c.status}</td>
			            <td>
						<button class="btn btn-sm btn-success me-2"
						        onclick="openEditCauseModal(${c.id})">
						    Edit
						</button>

						<button class="btn btn-sm btn-danger"
						        onclick="deleteCause(${c.id})">
						    Delete
						</button>
			            </td>
			        </tr>
			    `;
			});
        })
        .catch(err => console.error("Causes load error:", err));
}


// ===============================
// DONATIONS (FIXED)
// ===============================
function loadDonationsList() {

    const tbody = document.getElementById("donationsTableBody");

    if (!tbody) {
        console.error("donationsTableBody not found");
        return;
    }

    fetch(`${BASE_URL}/api/donations`)
        .then(r => r.json())
        .then(data => {

            console.log("Donations:", data);

            tbody.innerHTML = "";

            data.forEach(d => {

                tbody.innerHTML += `
                <tr>
                    <td>${d.id}</td>
                    <td>${d.donorName || "-"}</td>
                    <td>${d.causeTitle || "-"}</td>
                    <td>Rs${d.amount || 0}</td>
                    <td>${d.donationDate || "-"}</td>
                    <td>${d.paymentMethod || "-"}</td>
                    <td>${safeText(d.message)}</td>
                </tr>
                `;

            });

        })
        .catch(err => {
            console.error("Donation Load Error:", err);
        });
}


// ===============================
// VOLUNTEERS (ACCEPT / REJECT)
// ===============================
function loadVolunteersList() {

    const tbody = document.getElementById("volunteersTableBody");

    fetch(`${BASE_URL}/api/volunteers`)
        .then(r => r.json())
        .then(data => {

            tbody.innerHTML = "";

			data.forEach(v => {

			    const badgeClass =
			        v.status === "APPROVED" ? "bg-success" :
			        v.status === "REJECTED" ? "bg-danger" :
			        "bg-warning";

			    let actionButtons = "";

			    if (!v.status || v.status === "PENDING") {

			        actionButtons = `
			            <button class="btn btn-sm btn-success"
			                onclick="updateVolunteerStatus(${v.id}, 'APPROVED')">
			                Accept
			            </button>

			            <button class="btn btn-sm btn-danger"
			                onclick="updateVolunteerStatus(${v.id}, 'REJECTED')">
			                Reject
			            </button>
			        `;
			    }

			    tbody.innerHTML += `
			    <tr>
			        <td>${v.id}</td>
			        <td>${v.name}</td>
			        <td>${v.email}</td>
			        <td>${v.phone}</td>
			        <td>${v.message || "-"}</td>

			        <td>
			            <span class="badge ${badgeClass}">
			                ${v.status || "PENDING"}
			            </span>
			        </td>

			        <td>
			            ${actionButtons}
			        </td>
			    </tr>`;
			});
        });
}


// ===============================
// UPDATE VOLUNTEER STATUS
// ===============================
function updateVolunteerStatus(id, status) {

    fetch(`${BASE_URL}/api/volunteers`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            id: id,
            status: status
        })
    })
    .then(r => r.json())
    .then(res => {

        if (res.success) {
            alert("Status updated successfully");
            loadVolunteersList();
            loadDashboardData();
        } else {
            alert(res.message || "Failed");
        }

    })
    .catch(err => {
        console.error(err);
        alert("Error updating volunteer status");
    });
}


// ===============================
// VISITOR MESSAGES (FIXED)
// ===============================
function loadQueriesList() {

    const tbody = document.getElementById("queriesTableBody");

    fetch(`${BASE_URL}/api/contacts`)
        .then(r => r.json())
        .then(data => {

            tbody.innerHTML = "";

            data.forEach(q => {
                tbody.innerHTML += `
                <tr>
                    <td>${q.id}</td>
                    <td>${q.name}</td>
                    <td>${q.subject}</td>
                    <td>${safeText(q.message)}</td>
                    <td>${formatDate(q)}</td>
                    <td>
						<button class="btn btn-danger btn-sm"
						        onclick="deleteQuery(${q.id})">
						     Delete
						</button>                    
					</td>
                </tr>`;
            });
        });
}


// ===============================
// DELETE QUERY
// ===============================
function deleteQuery(id) {

    if (!confirm("Delete this message?")) return;

    fetch(`${BASE_URL}/api/contacts?id=${id}`, {
        method: "DELETE"
    })
    .then(() => loadQueriesList());
}


// ===============================
// CAUSE CRUD (SIMPLE)
// ===============================
function handleCauseSubmit(e) {
    e.preventDefault();

    const id = document.getElementById("causeId").value;

    const data = {
        id: id ? parseInt(id) : null,
        title: document.getElementById("causeTitleInput").value,
        description: document.getElementById("causeDescInput").value,
        imageUrl: document.getElementById("causeImgInput").value,
        goalAmount: document.getElementById("causeGoalInput").value,
        status: document.getElementById("causeStatusInput").value
    };

    fetch(`${BASE_URL}/api/causes`, {
        method: id ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    }).then(() => {
        loadCausesList();
        loadDashboardData();
    });
}


// ===============================
// LOGOUT
// ===============================
function handleLogout() {
    fetch(`${BASE_URL}/api/auth/logout`, { method: "POST" })
        .then(() => window.location.href = "login.html");
}


// ===============================
// OPEN ADD CAUSE MODAL
// ===============================
function openAddCauseModal() {

    document.getElementById("causeForm").reset();

    document.getElementById("causeId").value = "";

    document.getElementById("causeModalTitle").innerText =
        "Add Charity Cause";

    document.getElementById("causeStatusWrapper")
        .classList.add("d-none");

    const modal = new bootstrap.Modal(
        document.getElementById("causeModal")
    );

    modal.show();
}


// ===============================
// OPEN EDIT CAUSE MODAL
// ===============================
function openEditCauseModal(id) {

    fetch(`${BASE_URL}/api/causes`)
        .then(r => r.json())
        .then(data => {

            const cause = data.find(c => c.id == id);

            if (!cause) {
                alert("Cause not found");
                return;
            }

            document.getElementById("causeId").value =
                cause.id;

            document.getElementById("causeTitleInput").value =
                cause.title || "";

            document.getElementById("causeDescInput").value =
                cause.description || "";

            document.getElementById("causeImgInput").value =
                cause.imageUrl || "";

            document.getElementById("causeGoalInput").value =
                cause.goalAmount || "";

            document.getElementById("causeStatusInput").value =
                cause.status || "active";

            document.getElementById("causeStatusWrapper")
                .classList.remove("d-none");

            document.getElementById("causeModalTitle").innerText =
                "Edit Charity Cause";

            const modal = new bootstrap.Modal(
                document.getElementById("causeModal")
            );

            modal.show();
        });
}


// ===============================
// DELETE CAUSE
// ===============================
function deleteCause(id) {

    if (!confirm("Are you sure you want to delete this cause?"))
        return;

    fetch(`${BASE_URL}/api/causes?id=${id}`, {
        method: "DELETE"
    })
    .then(() => {
        loadCausesList();
        loadDashboardData();
    })
    .catch(err => {
        console.error(err);
        alert("Failed to delete cause");
    });
}
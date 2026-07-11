// Charifit Donation Form Logic
const BASE_URL = "";

document.addEventListener("DOMContentLoaded", () => {
    const amountButtons = document.querySelectorAll(".donate-amount-btn");
    const customAmountInput = document.getElementById("customAmount");
    const causeSelect = document.getElementById("donationCause");
    const paymentMethods = document.querySelectorAll(".payment-method-card");
    const donationForm = document.getElementById("donationForm");
    const selectedMethodInput = document.getElementById("selectedPaymentMethod");

    // 1. Load active causes into dropdown
    loadCausesForDonation(causeSelect);

    // 2. Amount quick selection buttons
    amountButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            amountButtons.forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
            customAmountInput.value = btn.dataset.amount;
        });
    });

    customAmountInput.addEventListener("input", () => {
        // Deselect quick buttons if user starts typing a custom amount not in quick list
        const val = customAmountInput.value;
        amountButtons.forEach(btn => {
            if (btn.dataset.amount === val) {
                btn.classList.add("active");
            } else {
                btn.classList.remove("active");
            }
        });
    });

    // 3. Payment Method switching
    paymentMethods.forEach(card => {
        card.addEventListener("click", () => {
            paymentMethods.forEach(c => c.classList.remove("active"));
            card.classList.add("active");
            selectedMethodInput.value = card.dataset.method;
        });
    });

    // 4. Handle Donation Submission
    if (donationForm) {
        donationForm.addEventListener("submit", handleDonationSubmit);
    }
	
	const username = localStorage.getItem("username");
	    const userEmail = localStorage.getItem("userEmail");

	    if (!username) {
	        alert("Please login first to donate");
	        window.location.href = "user-login.html";
	        return;
	    }

	    // Auto-fill email
	    if (userEmail) {
	        document.getElementById("donorEmail").value = userEmail;
	    }

	    console.log("Donation page loaded for:", username);
});

// Load causes into dropdown select list
function loadCausesForDonation(selectElement) {
    if (!selectElement) return;

fetch(`${BASE_URL}/api/causes`)
        .then(response => response.json())
        .then(causes => {
            selectElement.innerHTML = `<option value="" disabled selected>Choose a cause to support...</option>`;
            
            // Filter active causes
            const activeCauses = causes.filter(c => c.status === "active");
            
            if (activeCauses.length === 0) {
                selectElement.innerHTML = `<option value="" disabled>No active causes available</option>`;
                return;
            }

            activeCauses.forEach(cause => {
                const opt = document.createElement("option");
                opt.value = cause.id;
                opt.textContent = cause.title;
                selectElement.appendChild(opt);
            });

            // Check URL parameters for pre-selected cause ID
            const urlParams = new URLSearchParams(window.location.search);
            const preselectedId = urlParams.get("causeId");
            if (preselectedId) {
                selectElement.value = preselectedId;
            }
        })
        .catch(err => {
            console.error("Error loading causes for dropdown:", err);
            selectElement.innerHTML = `<option value="" disabled>Error loading causes. Check server.</option>`;
        });
}

// Submit Donation AJAX
function handleDonationSubmit(e) {
    e.preventDefault();

    const submitBtn = e.target.querySelector("button[type='submit']");
    const originalText = submitBtn.innerText;
    submitBtn.disabled = true;
    submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing payment...`;

    const amount = parseFloat(document.getElementById("customAmount").value);
    const causeId = parseInt(document.getElementById("donationCause").value);
    const donorName = document.getElementById("donorName").value;
    const donorEmail = document.getElementById("donorEmail").value;
    const paymentMethod = document.getElementById("selectedPaymentMethod").value;
    const message = document.getElementById("donorMessage").value;

    // Client-side validations
    if (isNaN(causeId) || causeId <= 0) {
        alertErrorDonate(e.target, "Please select a valid cause.");
        resetSubmitButton(submitBtn, originalText);
        return;
    }
    if (isNaN(amount) || amount <= 0) {
        alertErrorDonate(e.target, "Please specify a donation amount greater than $0.");
        resetSubmitButton(submitBtn, originalText);
        return;
    }

    const donationData = {
        causeId: causeId,
        donorName: donorName,
        donorEmail: donorEmail,
        amount: amount,
        paymentMethod: paymentMethod,
        message: message
    };

fetch(`${BASE_URL}/api/donations`, {
	        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(donationData)
    })
    .then(response => response.json())
	.then(data => {

	    console.log("Response Data:", data);

	    resetSubmitButton(submitBtn, originalText);

		if (data.success) {

		    const successModalEl =
		        document.getElementById("successModal");

		    if (successModalEl) {

		        const modalCauseName =
		            document.getElementById("modalCauseName");

		        const modalAmount =
		            document.getElementById("modalAmount");

		        const causeSelect =
		            document.getElementById("donationCause");

		        if (modalCauseName) {
		            modalCauseName.innerText =
		                causeSelect.options[causeSelect.selectedIndex].text;
		        }

		        if (modalAmount) {
		            modalAmount.innerText =
		                "Rs" + amount.toLocaleString(undefined, {
		                    minimumFractionDigits: 2,
		                    maximumFractionDigits: 2
		                });
		        }

		        // Certificate Download Button
		        const certificateBtn =
		            document.getElementById("downloadCertificateBtn");

					if (certificateBtn) {

					    const causeName =
					        causeSelect.options[causeSelect.selectedIndex].text;

					    const certificateUrl =
					        `${BASE_URL}/certificate?name=${encodeURIComponent(donorName)}&amount=${amount}&cause=${encodeURIComponent(causeName)}`;

					    console.log("Donor Name:", donorName);
					    console.log("Cause Name:", causeName);
					    console.log("Amount:", amount);
					    console.log("Certificate URL:", certificateUrl);

					    certificateBtn.href = certificateUrl;
						
						console.log("Button Found:", certificateBtn);
						console.log("Final Href:", certificateBtn.href);
					}

		        const myModal =
		            new bootstrap.Modal(successModalEl);

		        myModal.show();

				successModalEl.addEventListener("hidden.bs.modal", () => {

				    const form = document.getElementById("donationForm");

				    if (form) {
				        form.reset();
				    }

				    document.querySelectorAll(".donate-amount-btn")
				        .forEach(btn => btn.classList.remove("active"));

				    const defaultBtn =
				        document.querySelector(
				            ".donate-amount-btn[data-amount='500']"
				        );

				    if (defaultBtn) {
				        defaultBtn.classList.add("active");
				    }

				    const amountInput =
				        document.getElementById("customAmount");

				    if (amountInput) {
				        amountInput.value = 500;
				    }
				});

		    } else {

		        alertSuccessDonate(e.target, data.message);
		        document.getElementById("donationForm").reset();

		    }

		} else {

		    alertErrorDonate(e.target, data.message);

		}
    })
    .catch(err => {
        resetSubmitButton(submitBtn, originalText);
        alertErrorDonate(e.target, "Failed to connect to the server. Please verify MySQL configuration.");
    });
}

function resetSubmitButton(btn, text) {
    btn.disabled = false;
    btn.innerText = text;
}

function alertSuccessDonate(form, message) {
    removeAlertsDonate(form);
    const alertDiv = document.createElement("div");
    alertDiv.className = "alert alert-success alert-dismissible fade show mt-3";
    alertDiv.innerHTML = `
        <strong>Thank you!</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    form.appendChild(alertDiv);
}

function alertErrorDonate(form, message) {
    removeAlertsDonate(form);
    const alertDiv = document.createElement("div");
    alertDiv.className = "alert alert-danger alert-dismissible fade show mt-3";
    alertDiv.innerHTML = `
        <strong>Payment Failed:</strong> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    form.appendChild(alertDiv);
}

function removeAlertsDonate(form) {
    const existingAlerts = form.querySelectorAll(".alert");
    existingAlerts.forEach(alert => alert.remove());
}

window.addEventListener("DOMContentLoaded", () => {

    const username = localStorage.getItem("username");

    if (!username) {
        alert("Please login first to donate");
        window.location.href = "user-login.html";
        return;
    }

    console.log("Donation page loaded for:", username);

});
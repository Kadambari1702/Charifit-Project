const BASE_URL = "";

document.addEventListener("DOMContentLoaded", () => {

    const email = localStorage.getItem("userEmail");

    console.log("User Email:", email);

    if (!email) {
        alert("Please login first");
        window.location.href = "user-login.html";
        return;
    }

    fetch(`${BASE_URL}/api/donations?action=mydonations&email=${encodeURIComponent(email)}`)
        .then(response => response.json())
        .then(donations => {

            console.log("Donations:", donations);

            const tableBody = document.getElementById("donationTableBody");

            tableBody.innerHTML = "";

            if (!donations || donations.length === 0) {
                tableBody.innerHTML = `
                    <tr>
                        <td colspan="3" class="text-center">
                            No donations found
                        </td>
                    </tr>
                `;
                return;
            }

			donations.sort((a, b) => {
			    const dateA = new Date(a.donationDate || a.date || a.createdAt || 0);
			    const dateB = new Date(b.donationDate || b.date || b.createdAt || 0);
			    return dateB - dateA;
			});
			
			donations.forEach(donation => {

			    const row = document.createElement("tr");

			    const date = formatDate(
			        donation.donationDate || donation.date || donation.createdAt
			    );

			    // Cause
			    const cause = document.createElement("td");
			    cause.textContent = donation.causeTitle || "-";

			    // Amount
			    const amount = document.createElement("td");
			    amount.textContent = "Rs " + donation.amount;

			    // Date
			    const dateTd = document.createElement("td");
			    dateTd.textContent = date;

			    // Certificate Button
			    const certificateTd = document.createElement("td");

			    certificateTd.innerHTML = `
			        <a
			            href="${BASE_URL}/certificate?name=${encodeURIComponent(donation.donorName)}&amount=${donation.amount}&cause=${encodeURIComponent(donation.causeTitle)}"
			            class="btn btn-success btn-sm"
			            target="_blank">
			            <i class="fa fa-download"></i> Download PDF
			        </a>
			    `;

			    row.appendChild(cause);
			    row.appendChild(amount);
			    row.appendChild(dateTd);
			    row.appendChild(certificateTd);

			    tableBody.appendChild(row);
			});
        })
        .catch(error => {
            console.error("Error loading donations:", error);

            document.getElementById("donationTableBody").innerHTML = `
                <tr>
                    <td colspan="3" class="text-danger text-center">
                        Failed to load donations
                    </td>
                </tr>
            `;
        });
});

function formatDate(dateValue) {
    if (!dateValue) return "-";

    const d = new Date(dateValue);

    if (isNaN(d.getTime())) {
        console.log("Invalid date received:", dateValue);
        return "-";
    }

    return d.toLocaleString();
}


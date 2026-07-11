package com.org.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

@WebServlet("/certificate")
public class CertificateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String donorName = request.getParameter("name");
        String amount = request.getParameter("amount");
        String cause = request.getParameter("cause");

        donorName = (donorName == null || donorName.trim().isEmpty())
                ? "Valued Donor" : donorName;

        amount = (amount == null || amount.trim().isEmpty())
                ? "0" : amount;

        cause = (cause == null || cause.trim().isEmpty())
                ? "General Donation" : cause;

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Certificate_" +
                        donorName.replaceAll(" ", "_") + ".pdf"
        );

        try {

            // ✅ Professional A4 layout (balanced margins)
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);

            PdfWriter writer =
                    PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            // =========================
            // CERTIFICATE META
            // =========================
            String certificateId =
                    "CERT-" +
                            UUID.randomUUID()
                                    .toString()
                                    .replace("-", "")
                                    .substring(0, 8)
                                    .toUpperCase();

            String donationDate = LocalDate.now().toString();

            // =========================
            // DOUBLE BORDER (PRO LOOK)
            // =========================
            PdfContentByte canvas = writer.getDirectContent();
            Rectangle page = document.getPageSize();

            // Outer border
            Rectangle outer = new Rectangle(
                    page.getLeft() + 10,
                    page.getBottom() + 10,
                    page.getRight() - 10,
                    page.getTop() - 10
            );
            outer.setBorder(Rectangle.BOX);
            outer.setBorderWidth(2f);
            outer.setBorderColor(new BaseColor(212, 175, 55));
            canvas.rectangle(outer);
            canvas.stroke();

            // Inner border
            Rectangle inner = new Rectangle(
                    page.getLeft() + 20,
                    page.getBottom() + 20,
                    page.getRight() - 20,
                    page.getTop() - 20
            );
            inner.setBorder(Rectangle.BOX);
            inner.setBorderWidth(1f);
            inner.setBorderColor(new BaseColor(184, 134, 11));
            canvas.rectangle(inner);
            canvas.stroke();

            // =========================
            // LOGO
            // =========================
            try {
                String logoPath = getServletContext().getRealPath("/images/logo.png");
                Image logo = Image.getInstance(logoPath);

                logo.scaleToFit(140, 70);
                logo.setAlignment(Element.ALIGN_CENTER);

                document.add(logo);

            } catch (Exception e) {
                Paragraph fallback = new Paragraph("CHARIFIT");
                fallback.setAlignment(Element.ALIGN_CENTER);
                document.add(fallback);
            }

            // spacing
            document.add(new Paragraph(" "));

            // =========================
            // TITLE
            // =========================
            Font titleFont = new Font(
                    Font.FontFamily.TIMES_ROMAN,
                    28,
                    Font.BOLD,
                    new BaseColor(184, 134, 11)
            );

            Paragraph title = new Paragraph("DONATION CERTIFICATE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            document.add(title);

            // =========================
            // INTRO
            // =========================
            Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 14);

            Paragraph intro = new Paragraph(
                    "This Certificate Is Proudly Presented To",
                    normalFont
            );
            intro.setAlignment(Element.ALIGN_CENTER);
            intro.setSpacingAfter(10f);
            document.add(intro);

            // =========================
            // DONOR NAME
            // =========================
            Font nameFont = new Font(
                    Font.FontFamily.TIMES_ROMAN,
                    30,
                    Font.BOLD,
                    new BaseColor(0, 102, 102)
            );

            Paragraph donor = new Paragraph(donorName.toUpperCase(), nameFont);
            donor.setAlignment(Element.ALIGN_CENTER);
            donor.setSpacingAfter(5f);
            document.add(donor);

            Paragraph line = new Paragraph("____________________________");
            line.setAlignment(Element.ALIGN_CENTER);
            line.setSpacingAfter(10f);
            document.add(line);

            // =========================
            // CAUSE
            // =========================
            Paragraph causeIntro = new Paragraph(
                    "For Generous Contribution Towards",
                    normalFont
            );
            causeIntro.setAlignment(Element.ALIGN_CENTER);
            document.add(causeIntro);

            Font causeFont = new Font(
                    Font.FontFamily.TIMES_ROMAN,
                    18,
                    Font.BOLD,
                    new BaseColor(34, 139, 34)
            );

            Paragraph causeText = new Paragraph(cause, causeFont);
            causeText.setAlignment(Element.ALIGN_CENTER);
            causeText.setSpacingAfter(5f);
            document.add(causeText);

            Paragraph appreciation = new Paragraph(
                    "In recognition of your valuable contribution towards creating a better future.",
                    new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC, BaseColor.DARK_GRAY)
            );
            appreciation.setAlignment(Element.ALIGN_CENTER);
            appreciation.setSpacingAfter(15f);
            document.add(appreciation);

            // =========================
            // DETAILS BOX (KEEP TOGETHER)
            // =========================
            Paragraph details = new Paragraph();

            Font detailTitle = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font detailFont = new Font(Font.FontFamily.HELVETICA, 12);

            Paragraph head = new Paragraph("CERTIFICATE DETAILS", detailTitle);
            head.setAlignment(Element.ALIGN_CENTER);

            Paragraph amt = new Paragraph("Donation Amount: Rs. " + amount, detailFont);
            amt.setAlignment(Element.ALIGN_CENTER);

            Paragraph date = new Paragraph("Donation Date: " + donationDate, detailFont);
            date.setAlignment(Element.ALIGN_CENTER);

            Paragraph cert = new Paragraph("Certificate ID: " + certificateId, detailFont);
            cert.setAlignment(Element.ALIGN_CENTER);

            details.add(head);
            details.add(amt);
            details.add(date);
            details.add(cert);

            details.setSpacingBefore(10f);
            details.setSpacingAfter(20f);
            details.setKeepTogether(true);

            document.add(details);

            // =========================
            // SIGNATURE (RIGHT SIDE)
            // =========================
            try {
                String signPath = getServletContext().getRealPath("/images/signature.png");
                Image sign = Image.getInstance(signPath);

                sign.scaleToFit(110, 60);
                sign.setAlignment(Element.ALIGN_RIGHT);

                document.add(sign);

            } catch (Exception e) {
                System.out.println("Signature missing");
            }

            Paragraph signText = new Paragraph(
                    "Authorized Signatory\nCharifit Foundation",
                    new Font(Font.FontFamily.HELVETICA, 11)
            );
            signText.setAlignment(Element.ALIGN_RIGHT);
            signText.setSpacingBefore(5f);
            document.add(signText);

            // =========================
            // FOOTER
            // =========================
            Paragraph footer = new Paragraph(
                    "\"Together We Create Positive Change In Society\"",
                    new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC, new BaseColor(100, 100, 100))
            );

            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20f);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error generating certificate");
        }
    }
}
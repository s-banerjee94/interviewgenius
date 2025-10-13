package in.connectwithsandeepan.interviewgenius.userservice.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for parsing PDF files using Apache PDFBox
 */
@Slf4j
@Component
public class PdfParserUtil {

    /**
     * Parses a PDF file from MultipartFile and prints the extracted text to console
     *
     * @param pdfFile The PDF file as MultipartFile
     * @return The extracted text from the PDF
     * @throws IOException if there's an error reading or parsing the PDF
     */
    public String parsePdfAndPrint(MultipartFile pdfFile) throws IOException {
        if (pdfFile == null || pdfFile.isEmpty()) {
            log.error("PDF file is null or empty");
            throw new IllegalArgumentException("PDF file cannot be null or empty");
        }

        // Validate that the file is actually a PDF
        String contentType = pdfFile.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            log.warn("File content type is not application/pdf: {}", contentType);
        }

        log.info("Starting to parse PDF file: {} (size: {} bytes)",
                pdfFile.getOriginalFilename(), pdfFile.getSize());

        String extractedText;

        try (InputStream inputStream = pdfFile.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            log.info("PDF loaded successfully. Number of pages: {}", document.getNumberOfPages());

            // Create PDFTextStripper to extract text
            PDFTextStripper pdfStripper = new PDFTextStripper();

            // Extract text from all pages
            extractedText = pdfStripper.getText(document);

            // Print to console
            System.out.println("========================================");
            System.out.println("PDF Content Extracted from: " + pdfFile.getOriginalFilename());
            System.out.println("========================================");
            System.out.println(extractedText);
            System.out.println("========================================");
            System.out.println("Total characters extracted: " + extractedText.length());
            System.out.println("========================================");

            log.info("PDF parsing completed successfully. Extracted {} characters",
                    extractedText.length());

        } catch (IOException e) {
            log.error("Error parsing PDF file: {}", e.getMessage(), e);
            throw new IOException("Failed to parse PDF: " + e.getMessage(), e);
        }

        return extractedText;
    }

    /**
     * Parses a specific page range from a PDF file
     *
     * @param pdfFile   The PDF file as MultipartFile
     * @param startPage The starting page number (1-based)
     * @param endPage   The ending page number (1-based)
     * @return The extracted text from the specified page range
     * @throws IOException if there's an error reading or parsing the PDF
     */
    public String parsePdfPageRange(MultipartFile pdfFile, int startPage, int endPage) throws IOException {
        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new IllegalArgumentException("PDF file cannot be null or empty");
        }

        if (startPage < 1 || endPage < startPage) {
            throw new IllegalArgumentException("Invalid page range");
        }

        String extractedText;

        try (InputStream inputStream = pdfFile.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            int totalPages = document.getNumberOfPages();

            if (endPage > totalPages) {
                log.warn("End page {} exceeds total pages {}. Adjusting to total pages.",
                        endPage, totalPages);
                endPage = totalPages;
            }

            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(startPage);
            pdfStripper.setEndPage(endPage);

            extractedText = pdfStripper.getText(document);

            System.out.println("========================================");
            System.out.println("PDF Content (Pages " + startPage + "-" + endPage + "): "
                    + pdfFile.getOriginalFilename());
            System.out.println("========================================");
            System.out.println(extractedText);
            System.out.println("========================================");

            log.info("Extracted {} characters from pages {}-{}",
                    extractedText.length(), startPage, endPage);

        } catch (IOException e) {
            log.error("Error parsing PDF page range: {}", e.getMessage(), e);
            throw new IOException("Failed to parse PDF page range: " + e.getMessage(), e);
        }

        return extractedText;
    }

    /**
     * Gets the number of pages in a PDF file
     *
     * @param pdfFile The PDF file as MultipartFile
     * @return The number of pages in the PDF
     * @throws IOException if there's an error reading the PDF
     */
    public int getPdfPageCount(MultipartFile pdfFile) throws IOException {
        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new IllegalArgumentException("PDF file cannot be null or empty");
        }

        try (InputStream inputStream = pdfFile.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            int pageCount = document.getNumberOfPages();
            log.info("PDF {} has {} pages", pdfFile.getOriginalFilename(), pageCount);
            return pageCount;

        } catch (IOException e) {
            log.error("Error reading PDF page count: {}", e.getMessage(), e);
            throw new IOException("Failed to get PDF page count: " + e.getMessage(), e);
        }
    }
}

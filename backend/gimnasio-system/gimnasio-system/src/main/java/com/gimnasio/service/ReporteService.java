package com.gimnasio.service;

import com.gimnasio.model.*;
import com.gimnasio.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteService {

    private final ClienteRepository clienteRepository;
    private final ReporteRepository reporteRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReporteService(ClienteRepository clienteRepository, ReporteRepository reporteRepository) {
        this.clienteRepository = clienteRepository;
        this.reporteRepository = reporteRepository;
    }

    public byte[] generarReporteClientePDF(Long clienteId) throws Exception {
        Cliente cliente = clienteRepository.findByIdWithMembresia(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(doc, baos);
        doc.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        Paragraph titulo = new Paragraph("REPORTE DE CLIENTE - SISTEMA GIMNASIO", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);
        doc.add(new Paragraph(" "));

        // Datos personales
        doc.add(new Paragraph("DATOS PERSONALES", headerFont));
        doc.add(new Paragraph("Nombre: " + cliente.getNombreCompleto(), bodyFont));
        doc.add(new Paragraph("Correo: " + cliente.getCorreo(), bodyFont));
        doc.add(new Paragraph("Documento: " + (cliente.getDocumento() != null ? cliente.getDocumento() : "N/A"), bodyFont));
        doc.add(new Paragraph("Teléfono: " + (cliente.getTelefono() != null ? cliente.getTelefono() : "N/A"), bodyFont));
        doc.add(new Paragraph(" "));

        // Datos físicos
        doc.add(new Paragraph("DATOS FÍSICOS", headerFont));
        doc.add(new Paragraph("Peso: " + (cliente.getPeso() != null ? cliente.getPeso() + " kg" : "N/A"), bodyFont));
        doc.add(new Paragraph("Altura: " + (cliente.getAltura() != null ? cliente.getAltura() + " m" : "N/A"), bodyFont));
        doc.add(new Paragraph("IMC: " + (cliente.getImc() != null ? cliente.getImc() : "N/A"), bodyFont));
        doc.add(new Paragraph("Objetivo: " + (cliente.getObjetivo() != null ? cliente.getObjetivo().toString() : "N/A"), bodyFont));
        doc.add(new Paragraph(" "));

        // Membresía
        doc.add(new Paragraph("MEMBRESÍA", headerFont));
        if (cliente.getMembresia() != null) {
            Membresia m = cliente.getMembresia();
            doc.add(new Paragraph("Plan: " + (m.getPlan() != null ? m.getPlan().getNombre() : "N/A"), bodyFont));
            doc.add(new Paragraph("Estado: " + m.getEstado().toString(), bodyFont));
            doc.add(new Paragraph("Vencimiento: " + (m.getFechaVencimiento() != null ? m.getFechaVencimiento().format(FMT) : "N/A"), bodyFont));
        } else {
            doc.add(new Paragraph("Sin membresía registrada", bodyFont));
        }
        doc.add(new Paragraph(" "));

        // Instructor
        doc.add(new Paragraph("INSTRUCTOR", headerFont));
        if (cliente.getInstructor() != null) {
            doc.add(new Paragraph("Instructor: " + cliente.getInstructor().getNombreCompleto(), bodyFont));
        } else {
            doc.add(new Paragraph("Sin instructor asignado", bodyFont));
        }

        doc.close();
        return baos.toByteArray();
    }

    public byte[] generarReporteTodosClientesCSV() throws Exception {
        List<Cliente> clientes = clienteRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos, java.nio.charset.StandardCharsets.UTF_8);
        CSVWriter csvWriter = new CSVWriter(osw);

        String[] header = {"ID", "Nombre", "Apellido", "Correo", "Documento", "Teléfono", "Objetivo", "Instructor"};
        csvWriter.writeNext(header);

        for (Cliente c : clientes) {
            String[] row = {
                    String.valueOf(c.getId()),
                    c.getNombre(),
                    c.getApellido(),
                    c.getCorreo(),
                    c.getDocumento() != null ? c.getDocumento() : "",
                    c.getTelefono() != null ? c.getTelefono() : "",
                    c.getObjetivo() != null ? c.getObjetivo().toString() : "",
                    c.getInstructor() != null ? c.getInstructor().getNombreCompleto() : ""
            };
            csvWriter.writeNext(row);
        }

        csvWriter.close();
        return baos.toByteArray();
    }
}
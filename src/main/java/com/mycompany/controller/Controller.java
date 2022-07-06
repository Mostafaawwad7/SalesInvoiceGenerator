
package com.mycompany.controller;

import com.mycompany.model.Invoice;
import com.mycompany.model.InvoicesTableModel;
import com.mycompany.model.Line;
import com.mycompany.model.LinesTableModel;
import com.mycompany.view.InvoiceDialog;
import com.mycompany.view.LineDialog;
import com.mycompany.view.ProjectFrame;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class Controller implements ActionListener,ListSelectionListener{
    private ProjectFrame frame;
    private InvoiceDialog invoiceDialog;
    private LineDialog lineDialog;
    

    public Controller (ProjectFrame frame)
    {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        switch (actionCommand){
            case "Create New Invoice":
                createNewInvoice();
                break;
            case "Delete Invoice":
                deleteInvoice();
                break;
            case "Load File":
                loadFile();
                break;
            case "Save File":
                saveFile();
                break;
            case "Create New Line":
                createNewLine();
                break;
            case "Delete Line":
                deleteLine();
                break;
            case "createInvoiceCancel":
                createInvoiceCancel();
                break;
            case "createInvoiceOK":
                createInvoiceOK();
                break;
            case "createLineOK":
                createLineOK();
                break;
            case "createLineCancel":
                createLineCancel();
                break;
                
        }
    }
       @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = frame.getInvoice_Table().getSelectedRow();
        if (selectedIndex != -1)
        {
            
            Invoice currentInvoice = frame.getInvoices().get(selectedIndex);
            frame.getInvoiceNumLabel().setText(""+ currentInvoice.getNum());
            frame.getInvoiceDateLabel().setText(currentInvoice.getDate());
            frame.getCustomerNameLabel().setText(currentInvoice.getCustomer());
            frame.getInvoiceTotalLabel().setText(""+currentInvoice.getInvoiceTotal());
            LinesTableModel linesTableModel = new LinesTableModel(currentInvoice.getLines());
            frame.getLine_Table().setModel(linesTableModel);
            linesTableModel.fireTableDataChanged();
    
        }
    }

    private void createNewInvoice() {
        invoiceDialog = new InvoiceDialog(frame);
        invoiceDialog.setVisible(true);
      
    }
       
    

 
    private void deleteInvoice() {
        int selectedRow = frame.getInvoice_Table().getSelectedRow();
            if (selectedRow != -1)
            {
                frame.getInvoices().remove(selectedRow);
                frame.getInvoicesTableModel().fireTableDataChanged();
            
            }
    }

    private void loadFile() {
       JFileChooser filechooser = new JFileChooser();
       try{
        int res = filechooser.showOpenDialog(frame);
       if(res == JFileChooser.APPROVE_OPTION)
       {
          File hf = filechooser.getSelectedFile();
          Path hp = Paths.get(hf.getAbsolutePath());
          List<String> headerLines = Files.readAllLines(hp);
          
          ArrayList<Invoice> invoiceArray = new ArrayList<>();
          for (String headerLine : headerLines)
          {
             String[] headerParts = headerLine.split(",");
             int invoiceNum = Integer.parseInt(headerParts[0]);
             String invoiceDate = headerParts[1];
             String customerName = headerParts[2];
             Invoice invoice = new Invoice(invoiceNum,invoiceDate,customerName);
             invoiceArray.add(invoice);
          }
          
          res = filechooser.showOpenDialog(frame);
           if (res == JFileChooser.APPROVE_OPTION)
           {
               File lineFile = filechooser.getSelectedFile();
               Path LinePath = Paths.get(lineFile.getAbsolutePath());
               List<String> lineLines = Files.readAllLines(LinePath);
               
               for(String lineLine : lineLines)
               {
                   String lineParts [] = lineLine.split(",");
                   int invoiceNum= Integer.parseInt(lineParts[0]);
                   String itemName = lineParts[1];
                   double itemPrice = Double.parseDouble(lineParts[2]);
                   int count = Integer.parseInt(lineParts[3]);
                   Invoice inv = null;
                   for(Invoice invoice : invoiceArray){
                       if(invoice.getNum() == invoiceNum)
                       {
                           inv = invoice;
                           break;
                       }
                   }
                   Line line = new Line(itemName,itemPrice,count,inv);
                   inv.getLines().add(line);
                   
               } 
               
               
           }
          frame.setInvoices(invoiceArray);
          InvoicesTableModel invoiceTableModel = new InvoicesTableModel(invoiceArray);
          frame.setInvoicesTableModel(invoiceTableModel);
          frame.getInvoice_Table().setModel(invoiceTableModel);
          frame.getInvoicesTableModel().fireTableDataChanged();
       }
       } catch (IOException ex){
           ex.printStackTrace();
       }
    }

    private void saveFile() 
    {
        ArrayList<Invoice> invoices = frame.getInvoices();
        String headers = "";
        String lines = "";
        for(Invoice invoice : invoices)
        {
            String invCSV = invoice.getAsCSV();
            headers += invCSV;
            headers += "\n";
            
            for(Line line : invoice.getLines())
            {
                String lineCSV = line.getAsCSV();
                lines += lineCSV;
                lines += "\n";
            }
            
        }
        
        try{
        JFileChooser fc= new JFileChooser();
        int result = fc.showSaveDialog(frame);
        if(result == JFileChooser.APPROVE_OPTION)
        {
            File headerFile = fc.getSelectedFile();
            FileWriter hfw = new FileWriter(headerFile);
            hfw.write(headers);
            hfw.flush();
            hfw.close();
            result = fc.showSaveDialog(frame);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                File lineFile = fc.getSelectedFile();
                FileWriter lfw = new FileWriter(lineFile);
                lfw.write(lines);
                lfw.flush();
                lfw.close();
                
            }
        }
        }catch(Exception ex) {
            
        }
    }

    private void createNewLine() {
        lineDialog = new LineDialog(frame);
        lineDialog.setVisible(true);
        
       
    }

    private void deleteLine() 
    {
        int selectedInv = frame.getInvoice_Table().getSelectedRow();
        int selectedRow = frame.getLine_Table().getSelectedRow();
            if (selectedInv != -1 && selectedRow != -1)
            {
                Invoice invoice = frame.getInvoices().get(selectedInv);
                invoice.getLines().remove(selectedRow);
                LinesTableModel linesTableModel = new LinesTableModel(invoice.getLines());
                frame.getLine_Table().setModel(linesTableModel);
                linesTableModel.fireTableDataChanged();
                frame.getInvoicesTableModel().fireTableDataChanged();
                
            
            }
    }

    private void createInvoiceCancel() {
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
        
    }

    private void createInvoiceOK() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = invoiceDialog.getInvDateField().getText();
        String customer = invoiceDialog.getCustNameField().getText();
        int num = frame.getNextInvoiceNum();
        try{
            df.parse(date);
            Invoice invoice = new Invoice(num,date,customer);
        frame.getInvoices().add(invoice);
        frame.getInvoicesTableModel().fireTableDataChanged();
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
        }catch(ParseException ex){
            JOptionPane.showMessageDialog(frame, "Wrong Date Format", "Error", JOptionPane.ERROR_MESSAGE);
            
            
        }
        
        
    }

    private void createLineOK() {
        String item = lineDialog.getItemNameField().getText();
        String countStr = lineDialog.getItemCountField().getText();
        String priceStr = lineDialog.getItemPriceField().getText();
        int count = Integer.parseInt(countStr);
        double price = Double.parseDouble(priceStr);
        int selectedInvoice = frame.getInvoice_Table().getSelectedRow();
        if (selectedInvoice != -1)
        {
            Invoice invoice = frame.getInvoices().get(selectedInvoice);
            Line line = new Line(item,price,count,invoice);
            invoice.getLines().add(line);
            LinesTableModel linesTableModel = (LinesTableModel) frame.getLine_Table().getModel();
            linesTableModel.fireTableDataChanged();
            frame.getInvoicesTableModel().fireTableDataChanged();
        }
        
        
        
        
        
         lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
        
    }

    private void createLineCancel() {
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
        
    }
    
}



 
 
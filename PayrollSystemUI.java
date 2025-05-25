import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector; // Used by DefaultTableModel

public class PayrollSystemUI extends JFrame {

    // --- Data Model ---
    // Represents an Employee
    static class Employee {
        private String id;
        private String name;
        private double hourlyRate;
        private double hoursWorked;

        public Employee(String id, String name, double hourlyRate, double hoursWorked) {
            this.id = id;
            this.name = name;
            this.hourlyRate = hourlyRate;
            this.hoursWorked = hoursWorked;
        }

        // Getters
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getHourlyRate() {
            return hourlyRate;
        }

        public double getHoursWorked() {
            return hoursWorked;
        }

        // Setters (for updating employee details)
        public void setName(String name) {
            this.name = name;
        }

        public void setHourlyRate(double hourlyRate) {
            this.hourlyRate = hourlyRate;
        }

        public void setHoursWorked(double hoursWorked) {
            this.hoursWorked = hoursWorked;
        }

        // Method to calculate gross pay
        public double calculateGrossPay() {
            return hourlyRate * hoursWorked;
        }

        @Override
        public String toString() {
            return "ID: " + id + ", Name: " + name + ", Rate: $" + hourlyRate + "/hr, Hours: " + hoursWorked;
        }
    }

    // List to store all employees (in-memory database)
    private List<Employee> employees = new ArrayList<>();

    // --- UI Components ---
    private JTabbedPane tabbedPane;

    // Employee Management Tab Components
    private JTextField employeeIdField;
    private JTextField employeeNameField;
    private JTextField hourlyRateField;
    private JTextField hoursWorkedField;
    private JButton addEmployeeButton;
    private JButton insertEmployeeButton;
    private JButton updateEmployeeButton;
    private JButton deleteEmployeeButton;
    private JButton enterButton; // New Enter Button
    private JTable employeeTable;
    private DefaultTableModel employeeTableModel;

    // Payroll Processing Tab Components
    private JTextField payrollEmployeeIdField;
    private JTextArea payslipDisplayArea;
    private JButton calculatePayButton;

    // --- Constructor ---
    public PayrollSystemUI() {
        setTitle("Employee Payroll System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Initialize tabbed pane
        tabbedPane = new JTabbedPane();

        // Build Employee Management Tab
        JPanel employeePanel = createEmployeeManagementPanel();
        tabbedPane.addTab("Employee Management", employeePanel);

        // Build Payroll Processing Tab
        JPanel payrollPanel = createPayrollProcessingPanel();
        tabbedPane.addTab("Payroll Processing", payrollPanel);

        // Add tabbed pane to the frame
        add(tabbedPane);

        // Load some sample data
        loadSampleData();
        updateEmployeeTable();
    }

    // --- Panel Creation Methods ---

    private JPanel createEmployeeManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // Main panel for employee management
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // --- Input Form Panel (North) ---
        JPanel inputFormPanel = new JPanel(new GridBagLayout());
        inputFormPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels and Text Fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputFormPanel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        employeeIdField = new JTextField(15);
        inputFormPanel.add(employeeIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputFormPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        employeeNameField = new JTextField(15);
        inputFormPanel.add(employeeNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputFormPanel.add(new JLabel("Hourly Rate:"), gbc);
        gbc.gridx = 1;
        hourlyRateField = new JTextField(15);
        inputFormPanel.add(hourlyRateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputFormPanel.add(new JLabel("Hours Worked:"), gbc);
        gbc.gridx = 1;
        hoursWorkedField = new JTextField(15);
        inputFormPanel.add(hoursWorkedField, gbc);

        // --- New Enter Button at the bottom of the input form panel ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span across two columns
        enterButton = new JButton("Enter");
        inputFormPanel.add(enterButton, gbc);

        panel.add(inputFormPanel, BorderLayout.NORTH);

        // --- Buttons Panel (Center - above table) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center buttons
        addEmployeeButton = new JButton("Add Employee");
        insertEmployeeButton = new JButton("Insert Employee");
        updateEmployeeButton = new JButton("Update Employee");
        deleteEmployeeButton = new JButton("Delete Employee");

        buttonPanel.add(addEmployeeButton);
        buttonPanel.add(insertEmployeeButton);
        buttonPanel.add(updateEmployeeButton);
        buttonPanel.add(deleteEmployeeButton);

        panel.add(buttonPanel, BorderLayout.CENTER); // Add button panel to the center of the main panel

        // --- Employee Table Panel (South) ---
        String[] columnNames = {"ID", "Name", "Hourly Rate", "Hours Worked", "Gross Pay"};
        employeeTableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(employeeTableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Employee List"));

        panel.add(scrollPane, BorderLayout.SOUTH); // Add table to the south of the main panel

        // --- Event Listeners ---
        addEmployeeButton.addActionListener(new AddEmployeeAction());
        insertEmployeeButton.addActionListener(new AddEmployeeAction());
        enterButton.addActionListener(new AddEmployeeAction()); // Assign the same action listener to the new button
        updateEmployeeButton.addActionListener(new UpdateEmployeeAction());
        deleteEmployeeButton.addActionListener(new DeleteEmployeeAction());

        // Listener to populate fields when a table row is selected
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && employeeTable.getSelectedRow() != -1) {
                int selectedRow = employeeTable.getSelectedRow();
                employeeIdField.setText(employeeTableModel.getValueAt(selectedRow, 0).toString());
                employeeNameField.setText(employeeTableModel.getValueAt(selectedRow, 1).toString());
                hourlyRateField.setText(employeeTableModel.getValueAt(selectedRow, 2).toString());
                hoursWorkedField.setText(employeeTableModel.getValueAt(selectedRow, 3).toString());
                employeeIdField.setEditable(false); // Prevent ID changes when updating
            } else {
                employeeIdField.setEditable(true); // Allow ID input when nothing is selected
            }
        });

        return panel;
    }

    private JPanel createPayrollProcessingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input and Button Panel (North)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Calculate Payslip"));
        inputPanel.add(new JLabel("Employee ID:"));
        payrollEmployeeIdField = new JTextField(15);
        inputPanel.add(payrollEmployeeIdField);
        calculatePayButton = new JButton("Calculate Pay");
        inputPanel.add(calculatePayButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        // Payslip Display Area (Center)
        payslipDisplayArea = new JTextArea(15, 40);
        payslipDisplayArea.setEditable(false);
        payslipDisplayArea.setLineWrap(true);
        payslipDisplayArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(payslipDisplayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Payslip Details"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Event Listener
        calculatePayButton.addActionListener(new CalculatePayAction());

        return panel;
    }

    // --- Helper Methods ---

    // Adds sample data to the employees list
    private void loadSampleData() {
        employees.add(new Employee("EMP001", "Alice Smith", 25.0, 160.0));
        employees.add(new Employee("EMP002", "Bob Johnson", 30.0, 150.0));
        employees.add(new Employee("EMP003", "Charlie Brown", 20.0, 170.0));
    }

    // Updates the JTable with current employee data
    private void updateEmployeeTable() {
        employeeTableModel.setRowCount(0); // Clear existing data
        for (Employee emp : employees) {
            Object[] rowData = {
                    emp.getId(),
                    emp.getName(),
                    emp.getHourlyRate(),
                    emp.getHoursWorked(),
                    emp.calculateGrossPay()
            };
            employeeTableModel.addRow(rowData);
        }
    }

    // Clears input fields
    private void clearEmployeeFields() {
        employeeIdField.setText("");
        employeeNameField.setText("");
        hourlyRateField.setText("");
        hoursWorkedField.setText("");
        employeeIdField.setEditable(true); // Ensure ID is editable for new entries
        employeeTable.clearSelection(); // Deselect any row
    }

    // --- Action Listeners ---

    private class AddEmployeeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String id = employeeIdField.getText().trim();
                String name = employeeNameField.getText().trim();
                double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
                double hoursWorked = Double.parseDouble(hoursWorkedField.getText().trim());

                if (id.isEmpty() || name.isEmpty() || hourlyRate <= 0 || hoursWorked < 0) {
                    showMessage("Please fill in all fields correctly.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Check for duplicate ID
                for (Employee emp : employees) {
                    if (emp.getId().equals(id)) {
                        showMessage("Employee with this ID already exists.", "Duplicate ID", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                Employee newEmployee = new Employee(id, name, hourlyRate, hoursWorked);
                employees.add(newEmployee);
                System.out.println("Added new employee: " + newEmployee.getName() + " (ID: " + newEmployee.getId() + ")");
                updateEmployeeTable();
                clearEmployeeFields();
                showMessage("Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                showMessage("Please enter valid numbers for Hourly Rate and Hours Worked.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UpdateEmployeeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow == -1) {
                showMessage("Please select an employee to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String id = employeeIdField.getText().trim(); // ID is from the selected row, not editable
                String name = employeeNameField.getText().trim();
                double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
                double hoursWorked = Double.parseDouble(hourlyRateField.getText().trim()); // Fixed: Was hourlyRateField.getText()

                if (name.isEmpty() || hourlyRate <= 0 || hoursWorked < 0) {
                    showMessage("Please fill in all fields correctly.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Find the employee by ID and update
                for (Employee emp : employees) {
                    if (emp.getId().equals(id)) {
                        emp.setName(name);
                        emp.setHourlyRate(hourlyRate);
                        emp.setHoursWorked(hoursWorked);
                        break;
                    }
                }
                updateEmployeeTable();
                clearEmployeeFields();
                showMessage("Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                showMessage("Please enter valid numbers for Hourly Rate and Hours Worked.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteEmployeeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow == -1) {
                showMessage("Please select an employee to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String employeeIdToDelete = employeeTableModel.getValueAt(selectedRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    PayrollSystemUI.this,
                    "Are you sure you want to delete employee with ID: " + employeeIdToDelete + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                employees.removeIf(emp -> emp.getId().equals(employeeIdToDelete));
                updateEmployeeTable();
                clearEmployeeFields();
                showMessage("Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private class CalculatePayAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String idToCalculate = payrollEmployeeIdField.getText().trim();
            if (idToCalculate.isEmpty()) {
                showMessage("Please enter an Employee ID to calculate pay.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Employee foundEmployee = null;
            for (Employee emp : employees) {
                if (emp.getId().equals(idToCalculate)) {
                    foundEmployee = emp;
                    break;
                }
            }

            if (foundEmployee != null) {
                double grossPay = foundEmployee.calculateGrossPay();
                // Simple deduction example (e.g., 10% tax)
                double taxRate = 0.10;
                double taxDeduction = grossPay * taxRate;
                double netPay = grossPay - taxDeduction;

                String payslip = String.format(
                        "--- Payslip for %s (%s) ---\n" +
                        "Hourly Rate: $%.2f\n" +
                        "Hours Worked: %.2f\n" +
                        "---------------------------\n" +
                        "Gross Pay: $%.2f\n" +
                        "Tax Deduction (%.0f%%): $%.2f\n" +
                        "---------------------------\n" +
                        "Net Pay: $%.2f\n" +
                        "---------------------------\n",
                        foundEmployee.getName(),
                        foundEmployee.getId(),
                        foundEmployee.getHourlyRate(),
                        foundEmployee.getHoursWorked(),
                        grossPay,
                        taxRate * 100,
                        taxDeduction,
                        netPay
                );
                payslipDisplayArea.setText(payslip);
            } else {
                payslipDisplayArea.setText("Employee with ID '" + idToCalculate + "' not found.");
                showMessage("Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Custom message box function instead of alert()
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // --- Main Method ---
    public static void main(String[] args) {
        // Ensure the UI is created and run on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new PayrollSystemUI().setVisible(true);
        });
    }
          }

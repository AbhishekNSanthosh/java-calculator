import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorApp extends JFrame {
    private JPanel menuPanel;

    public CalculatorApp() {
        setTitle("Calculator");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createMenuPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(menuPanel, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent componentEvent) {
                if (menuPanel.getComponentCount() > 0) {
                    updateButtonPanelLayout();
                }
            }
        });
    }

    protected void createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(2, 1, 10, 10));

        addButton("Simple Calculator", e -> showCalculator(new SimpleCalculator()));
        addButton("Scientific Calculator", e -> showCalculator(new ScientificCalculator()));
    }

    protected void addButton(String label, ActionListener listener) {
        JButton button = createMenuButton(label);
        button.addActionListener(listener);
        menuPanel.add(button);
    }

    private JButton createMenuButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.PLAIN, 20));
        button.setBackground(new Color(70, 130, 180)); // Dodger Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void updateButtonPanelLayout() {
        Component[] components = menuPanel.getComponents();
        if (components.length > 0 && components[0] instanceof JButton) {
            JButton sampleButton = (JButton) components[0];
            int preferredWidth = sampleButton.getPreferredSize().width;
            int availableWidth = getWidth() - 20; // Adjust for padding

            int columns = Math.max(1, availableWidth / preferredWidth);
            menuPanel.setLayout(new GridLayout(0, columns, 10, 10));

            menuPanel.revalidate();
            menuPanel.repaint();
        }
    }

    private void showCalculator(Calculator calculator) {
        calculator.setVisible(true);
        calculator.setLocationRelativeTo(null);
        calculator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dispose(); // Close the menu
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorApp().setVisible(true));
    }
}

class Calculator extends JFrame {
    JTextField displayField;
    String currentInput = "";
    double result = 0;
    char lastOperator = ' ';
    boolean newInput = true;

    public Calculator(String title) {
        setTitle(title);
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        displayField = createDisplayField();
        JPanel buttonPanel = createButtonPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(displayField, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.CENTER);
    }

    private JTextField createDisplayField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setFont(new Font("Arial", Font.PLAIN, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return field;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4, 5, 5));

        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+" // Add "=" button
        };

        for (String label : buttonLabels) {
            JButton button = createButton(label);
            button.addActionListener(this::handleButtonClick);
            panel.add(button);
        }

        return panel;
    }

   protected JButton createButton(String label) {
    JButton button = new JButton(label);
    button.setFont(new Font("Arial", Font.PLAIN, 18));
    button.setBackground(new Color(100, 149, 237));
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    return button;
}


    void handleButtonClick(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        String command = source.getText();

        try {
            switch (command) {
                case "=":
                    calculateResult();
                    break;
                case "C":
                    resetCalculator();
                    break;
                default:
                    handleInput(command);
            }
        } catch (NumberFormatException ex) {
            showError("Invalid number format!");
            resetCalculator();
        } catch (ArithmeticException ex) {
            showError("Arithmetic error: " + ex.getMessage());
            resetCalculator();
        } catch (Exception ex) {
            showError("An error occurred: " + ex.getMessage());
            resetCalculator();
        }
    }

    protected void calculateResult() {
        if (!currentInput.isEmpty()) {
            double currentNumber = Double.parseDouble(currentInput);
            switch (lastOperator) {
                case '+':
                    result += currentNumber;
                    break;
                case '-':
                    result -= currentNumber;
                    break;
                case '*':
                    result *= currentNumber;
                    break;
                case '/':
                    if (currentNumber != 0) {
                        result /= currentNumber;
                    } else {
                        showError("Error: Cannot divide by zero");
                        resetCalculator();
                        return;
                    }
                    break;
            }
            displayResult();
            resetInput();
        } else {
            showError("No operation entered!");
            resetCalculator();
        }
    }

    protected void handleInput(String input) {
        if (newInput) {
            currentInput = "";
            newInput = false;
        }
        currentInput += input;
        displayField.setText(currentInput);
    }

    protected void displayResult() {
        displayField.setText(String.valueOf(result));
    }

    protected void resetInput() {
        currentInput = "";
    }

    protected void resetCalculator() {
        currentInput = "";
        result = 0;
        lastOperator = ' ';
        newInput = true;
        displayField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

class SimpleCalculator extends Calculator {
    public SimpleCalculator() {
        super("Simple Calculator");
    }
}

class ScientificCalculator extends Calculator {
    public ScientificCalculator() {
        super("Scientific Calculator");
        addScientificButtons();
    }

    private void addScientificButtons() {
        String[] scientificButtons = {
                "sin", "cos", "sqrt",
                "log", "x^2", "x^n"
        };

        JPanel scientificPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        for (String label : scientificButtons) {
            JButton button = createButton(label);
            button.addActionListener(this::handleScientificButtonClick);
            scientificPanel.add(button);
        }

        getContentPane().add(scientificPanel, BorderLayout.SOUTH);
    }

    void handleScientificButtonClick(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        String command = source.getText();

        try {
            switch (command) {
                case "sin":
                    result = Math.sin(Math.toRadians(Double.parseDouble(currentInput)));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(Double.parseDouble(currentInput)));
                    break;
                case "sqrt":
                    result = Math.sqrt(Double.parseDouble(currentInput));
                    break;
                case "log":
                    result = Math.log10(Double.parseDouble(currentInput));
                    break;
                case "x^2":
                    result = Math.pow(Double.parseDouble(currentInput), 2);
                    break;
                case "x^n":
                    lastOperator = '^';
                    handleInput("^");
                    return; // Do not reset input, wait for the second number
            }
            displayResult();
            resetInput();
        } catch (NumberFormatException ex) {
            
            resetCalculator();
        } catch (ArithmeticException ex) {
        
            resetCalculator();
        } catch (Exception ex) {
           
            resetCalculator();
        }
    }
}

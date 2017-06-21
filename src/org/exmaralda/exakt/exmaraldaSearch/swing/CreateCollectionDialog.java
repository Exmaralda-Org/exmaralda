package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author KJ
 */
public class CreateCollectionDialog extends javax.swing.JDialog {

    private JButton selectOutputDirectoryButton,
            selectTemplateFileButton,
            okButton,
            cancelButton;
    private JCheckBox resetTimeCheckbox;
    private JFormattedTextField leftContextFormattedTextField,
            rightContextFormattedTextField,
            annotationTextFormattedTextField;
    private JLabel leftContextLabel,
            rightContextLabel,
            annotationTextLabel,
            resetTimeLabel,
            selectOutputDirectoryLabel,
            outputDirectoryLabel,
            selectTemplateFileLabel,
            templateFileLabel;
    private Integer leftContext,
            rightContext;
    private Boolean resetTime;
    private Boolean dialogApproved = false;

    private String outputDirectory, templateFile, annotationText;


    /**
     * Creates new form CreateCollectionDialog
     */
    public CreateCollectionDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    private void initComponents() {

        NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        //formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        leftContextLabel = new JLabel("Left context (in sec.)");
        rightContextLabel = new JLabel("Right context (in sec.)");
        annotationTextLabel = new JLabel("(Optional) Annotate matches with (use # for numbering)");
        resetTimeLabel = new JLabel("Reset start time to 0.0");
        selectOutputDirectoryLabel = new JLabel("Output directory");
        selectTemplateFileLabel = new JLabel("(Optional) Template file");
        leftContextFormattedTextField = new JFormattedTextField(formatter);
        leftContextFormattedTextField.setText("");
        rightContextFormattedTextField = new JFormattedTextField(formatter);
        rightContextFormattedTextField.setText("");
        annotationTextFormattedTextField = new JFormattedTextField();
        annotationTextFormattedTextField.setText("");
        resetTimeCheckbox = new JCheckBox();
        selectOutputDirectoryButton = new JButton("Select ...");
        selectTemplateFileButton = new JButton("Select ...");
        outputDirectoryLabel = new JLabel("");
        templateFileLabel = new JLabel("");
        okButton = new JButton("OK");
        //okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {

                if(
                    !leftContextFormattedTextField.getText().equals("") &&
                    !rightContextFormattedTextField.getText().equals("") &&
                    !outputDirectoryLabel.getText().equals("")
                ){

                    //set bool that dialog was closed with valid parameters
                    //program continues in CreateCollectionAction
                    dialogApproved = true;
                    dispose();

                    //show message that collection being generated
                    JOptionPane.showMessageDialog(null, "The collection will be generated in "+outputDirectoryLabel.getText()+" now. This may take several minutes.", "Note", JOptionPane.INFORMATION_MESSAGE);

                } else{
                    //open alert box
                    JOptionPane.showMessageDialog(null, "Please provide valid parameters.", "Warning", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                dispose();
            }
        });

        selectOutputDirectoryButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String directory = outputDirectory(".");
                if (directory != null) {
                    outputDirectoryLabel.setText(directory);
                }
            }
        });

        selectTemplateFileButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String template = templateFile(".");
                if (template != null) {
                    templateFileLabel.setText(template);
                }
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create collection");
        setModal(true);
        setResizable(true);
        setLocationRelativeTo(getParent());

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(leftContextLabel, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                        .addComponent(rightContextLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(annotationTextLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(resetTimeLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectOutputDirectoryLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectTemplateFileLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(selectOutputDirectoryButton)
                    .addComponent(selectTemplateFileButton)
                    .addComponent(resetTimeCheckbox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(outputDirectoryLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                        .addComponent(templateFileLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                        .addComponent(annotationTextFormattedTextField, GroupLayout.Alignment.LEADING)
                        .addComponent(rightContextFormattedTextField, GroupLayout.Alignment.LEADING)
                        .addComponent(leftContextFormattedTextField, GroupLayout.Alignment.LEADING)
                    )
                )
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            )
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(leftContextLabel)
                    .addComponent(leftContextFormattedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(rightContextLabel)
                    .addComponent(rightContextFormattedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(annotationTextLabel)
                    .addComponent(annotationTextFormattedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(resetTimeLabel)
                    .addComponent(resetTimeCheckbox)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(selectOutputDirectoryLabel)
                    .addComponent(selectOutputDirectoryButton)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputDirectoryLabel)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(selectTemplateFileLabel)
                    .addComponent(selectTemplateFileButton)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(templateFileLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                )
                .addContainerGap(26, Short.MAX_VALUE)
            )
        );

        pack();
    }

    public String outputDirectory(String startDirectory) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(startDirectory));
        fileChooser.setDialogTitle("Choose the output directoy");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    public String templateFile(String startDirectory) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {

           public String getDescription() {
               return "EXMARaLDA Tier Template (*.xml)";
           }

           public boolean accept(File f) {
               if (f.isDirectory()) {
                   return true;
               } else {
                   String filename = f.getName().toLowerCase();
                   return filename.endsWith(".xml") ;
               }
           }
        });
        fileChooser.setCurrentDirectory(new File(startDirectory));
        fileChooser.setDialogTitle("Choose the template file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }

    public Integer getLeftContext() {
        leftContext = Integer.valueOf(leftContextFormattedTextField.getText());
        return leftContext;
    }

    public Integer getRightContext() {
        rightContext = Integer.valueOf(rightContextFormattedTextField.getText());
        return rightContext;
    }

    public String getAnnotationText() {
        annotationText = String.valueOf(annotationTextFormattedTextField.getText());
        return annotationText;
    }

    public Boolean getResetTime() {
        resetTime = resetTimeCheckbox.isSelected();
        return resetTime;
    }

    public String getOutputDirectory() {
        outputDirectory = outputDirectoryLabel.getText();
        return outputDirectory;
    }

    public String getTemplateFile() {
        templateFile = templateFileLabel.getText();
        return templateFile;
    }

    public Boolean isDialogApproved() {
        return dialogApproved;
    }
}


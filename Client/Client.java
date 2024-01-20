import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {

        // Accessed from within inner class needs to be final or effectively final.
        final File[][] filesToSend = {null};

        // Set the frame to house everything.
        JFrame jFrame = new JFrame("FTP Server");
        // Set the size of the frame.
        jFrame.setSize(450, 450);
        // Make the layout to be box layout that places its children on top of each other.
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        // Make it so when the frame is closed the program exits successfully.
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title above panel.
        JLabel jlTitle = new JLabel("File Sender");
        // Change the font family, size, and style.
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        // Add a border around the label for spacing.
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        // Make it so the title is centered horizontally.
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label that has the file name.
        JLabel jlFileName = new JLabel("Choose a file to send.");
        // Change the font.
        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
        // Make a border for spacing.
        jlFileName.setBorder(new EmptyBorder(50, 0, 0, 0));
        // Center the label on the x axis (horizontally).
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel that contains the buttons.
        JPanel jpButton = new JPanel();
        // Border for panel that houses buttons.
        jpButton.setBorder(new EmptyBorder(75, 0, 10, 0));
        // Create send file button.
        JButton jbSendFile = new JButton("Send File");
        // Set preferred size works for layout containers.
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        // Change the font style, type, and size for the button.
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 20));
        // Make the second button to choose a file.
        JButton jbChooseFile = new JButton("Choose File");
        // Set the size which must be preferred size for within a container.
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        // Set the font for the button.
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 20));

        // Add the buttons to the panel.
        jpButton.add(jbSendFile);
        jpButton.add(jbChooseFile);

        // Button action for choosing the file.
        // This is an inner class so we need the filesToSend to be final.
        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser to open the dialog to choose a file.
                JFileChooser jFileChooser = new JFileChooser();
                // Set the title of the dialog.
                jFileChooser.setDialogTitle("Choose files to send.");
                // Allow multiple file selection.
                jFileChooser.setMultiSelectionEnabled(true);

                // Show the dialog and if a file is chosen from the file chooser execute the following statements.
                if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    // Get the selected files.
                    File[] selectedFiles = jFileChooser.getSelectedFiles();
                    // Update the filesToSend array.
                    filesToSend[0] = selectedFiles;
                    // Update the label to display the selected files.
                    String fileNames = getSelectedFileNames(selectedFiles);
                    jlFileName.setText("The files you want to send are: " + fileNames);
                }
            }
        });

        // Sends the files when the button is clicked.
        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If no files have been selected then display this message.
                if (filesToSend[0] == null || filesToSend[0].length == 0) {
                    jlFileName.setText("Please choose at least one file to send first!");
                } else {
                    // Iterate over the selected files and send each one.
                    for (File file : filesToSend[0]) {
                        try {
                            // Create an input stream into the file you want to send.
                            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                            // Create a socket connection to connect with the server.
                            Socket socket = new Socket("localhost", 1234);
                            // Create an output stream to write to write to the server over the socket connection.
                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            // Get the name of the file you want to send and store it in filename.
                            String fileName = file.getName();
                            // Convert the name of the file into an array of bytes to be sent to the server.
                            byte[] fileNameBytes = fileName.getBytes();
                            // Create a byte array the size of the file so don't send too little or too much data to the server.
                            byte[] fileBytes = new byte[(int) file.length()];
                            // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
                            fileInputStream.read(fileBytes);
                            // Send the length of the name of the file so server knows when to stop reading.
                            dataOutputStream.writeInt(fileNameBytes.length);
                            // Send the file name.
                            dataOutputStream.write(fileNameBytes);
                            // Send the length of the byte array so the server knows when to stop reading.
                            dataOutputStream.writeInt(fileBytes.length);
                            // Send the actual file.
                            dataOutputStream.write(fileBytes);
                            // Close the socket after sending the file.
                            socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    // Display a success message after sending the files.
                    jlFileName.setText("Files sent successfully!");
                }
            }
        });

        // Add everything to the frame and make it visible.
        jFrame.add(jlTitle);
        jFrame.add(jlFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);
    }

    // Helper method to get the names of selected files.
    private static String getSelectedFileNames(File[] selectedFiles) {
        StringBuilder fileNames = new StringBuilder();
        for (File file : selectedFiles) {
            fileNames.append(file.getName()).append(", ");
        }
        // Remove the trailing comma and space.
        return fileNames.substring(0, fileNames.length() - 2);
    }
}

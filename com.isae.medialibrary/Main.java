package com.isae.medialibrary;

import com.isae.medialibrary.service.MediaLibrary;
import com.isae.medialibrary.view.AdminLoginFrame;
import com.isae.medialibrary.view.StudentLoginFrame;
import com.isae.medialibrary.util.LogUtil;

import javax.swing.*;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = LogUtil.getLogger(Main.class);

    public static void main(String[] args) {
        MediaLibrary library = new MediaLibrary();
        library.loadData();

        SwingUtilities.invokeLater(() -> {
            String[] options = {"Student", "Administrator"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Select login mode:", "Media Library",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            if (choice == 0) {
                new StudentLoginFrame(library).setVisible(true);
            } else if (choice == 1) {
                new AdminLoginFrame(library).setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
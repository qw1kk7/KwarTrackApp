package com.mycompany.labopr.ui.dialogs;

import com.mycompany.labopr.utils.UserAuth;
import com.mycompany.labopr.views.Landing;
import javax.swing.*;

public class AuthDialogs {

    public static void handleLogin(JFrame parent) {
        String user = JOptionPane.showInputDialog(parent, "Enter username:");
        if (user == null) return;

        JPasswordField pf = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(parent, pf, "Enter password:", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String pass = new String(pf.getPassword());
        if (UserAuth.login(user, pass)) {
            JOptionPane.showMessageDialog(parent, "Login successful!");
            parent.dispose();
            new Landing().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(parent, "Invalid credentials.");
        }
    }

    public static void handleSignUp(JFrame parent) {
        String user = JOptionPane.showInputDialog(parent, "Choose a username:");
        if (user == null) return;

        JPasswordField pf = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(parent, pf, "Choose a password:", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String pass = new String(pf.getPassword());
        if (UserAuth.signUp(user, pass)) {
            JOptionPane.showMessageDialog(parent, "Sign up successful!");
        } else {
            JOptionPane.showMessageDialog(parent, "Username already exists!");
        }
    }
}
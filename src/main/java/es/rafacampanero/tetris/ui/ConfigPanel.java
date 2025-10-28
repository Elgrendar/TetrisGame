package es.rafacampanero.tetris.ui;

import es.rafacampanero.tetris.App;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Panel simple de configuración: por ahora permite seleccionar idioma.
 */
public class ConfigPanel extends JPanel {

    public ConfigPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.DARK_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);

        JLabel lbl = new JLabel(App.mensajes.getString("menu.config"));
        lbl.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0;
        add(lbl, gbc);

        String[] languages = {"es", "en"};
        JComboBox<String> combo = new JComboBox<>(languages);
        gbc.gridy = 1;
        add(combo, gbc);

        JButton apply = new JButton("Apply");
        gbc.gridy = 2;
        add(apply, gbc);

        apply.addActionListener(e -> {
            String lang = (String) combo.getSelectedItem();
            Locale locale = new Locale(lang);
            App.mensajes = java.util.ResourceBundle.getBundle("languages.strings", locale);
            JOptionPane.showMessageDialog(this, "Idioma cambiado. Reinicia la app para aplicar completamente.");
        });
    }
}

package labo4;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;

public class InterfaceRecherchePays extends JFrame {

    private JPanel panelRecherche = new JPanel(new FlowLayout());

    private JComboBox<String> continents = new JComboBox<>();
    private JComboBox<String> langages = new JComboBox<>();
    private JButton createXSL = new JButton("Générer XSL");
    private JTextField superficieMin = new JTextField(5);
    private JTextField superficieMax = new JTextField(5);

    public InterfaceRecherchePays(File xmlFile) {

        createXSL.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {

                super.mouseClicked(event);

                // Création des fichiers XSL selon ce qui est demandé

                /** A compléter... **/
                try {
                    org.w3c.dom.Document document = DocumentBuilderFactory
                            .newInstance()
                            .newDocumentBuilder()
                            .newDocument();

                    org.w3c.dom.Element racine = document.createElement("xsl:stylesheet");
                    racine.setAttribute("version", "1.0");
                    racine.setAttribute("xmlns:xsl", "http://www.w3.org/1999/XSL/Transform");

                    // output element
                    org.w3c.dom.Element output = document.createElement("xsl:output");
                    output.setAttribute("method", "html");
                    output.setAttribute("encoding", "UTF-8");
                    output.setAttribute("doctype-public", "-//W3C//DTD HTML 4.01//EN");
                    output.setAttribute("doctype-system", "http://www.w3.org/TR/html4/strict.dtd");
                    output.setAttribute("indent", "yes");

                    racine.appendChild(output);

                    org.w3c.dom.Element template = document.createElement("xsl:template");
                    template.setAttribute("match", "/");

                    org.w3c.dom.Element html = document.createElement("html");
                    org.w3c.dom.Element head = document.createElement("head");
                    org.w3c.dom.Element body = document.createElement("body");

                    org.w3c.dom.Element ul = document.createElement("ul");
                    org.w3c.dom.Element foreach = document.createElement("xsl:for-each");
                    foreach.setAttribute("select", "/countries/element");

                    org.w3c.dom.Element li = document.createElement("li");
                    org.w3c.dom.Element valueOf = document.createElement("xsl:value-of");
                    valueOf.setAttribute("select", "translations/fr");

                    li.appendChild(valueOf);
                    foreach.appendChild(li);
                    ul.appendChild(foreach);
                    body.appendChild(ul);

                    body.appendChild(ul);

                    html.appendChild(head);
                    html.appendChild(body);

                    template.appendChild(html);

                    racine.appendChild(output);
                    racine.appendChild(template);

                    // Ecriture de la racine dans le document
                    document.appendChild(racine);

                    // Ecriture dans le fichier XSL
                    DOMSource source = new DOMSource(document);
                    FileWriter writer = new FileWriter(new File("countries.xsl"));
                    StreamResult result = new StreamResult(writer);


                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                    transformer.transform(source, result);

                } catch (ParserConfigurationException
                        | IOException
                        | TransformerException e) {
                    e.printStackTrace();
                }

                System.out.println("DONE");
            }

        });

        /**
         * A compléter : Remplissage des listes de recherche (avec les continents et les langues parlées dans l'ordre alphabétique)
         */
        TreeSet<String> c = new TreeSet<>();
        TreeSet<String> l = new TreeSet<>();

        SAXBuilder saxBuilder = new SAXBuilder();

        try {
            Document document = saxBuilder.build(xmlFile);
            Element rootElement = document.getRootElement();

            for (Element element : rootElement.getChildren("element")) {

                c.add(element.getChildText("region"));

                for (Element language : element.getChild("languages").getChildren("element")) {
                    l.add(language.getChildText("name"));
                }
            }
        }

        catch (IOException | JDOMException io) {
            System.out.println(io.getMessage());
            return;
        }

        for (String continent : c) {
            continents.addItem(continent);
        }

        for (String language : l) {
            langages.addItem(language);
        }

        setLayout(new BorderLayout());

        panelRecherche.add(new JLabel("Choix d'un continent"));
        panelRecherche.add(continents);

        panelRecherche.add(new JLabel("Choix d'une langue"));
        panelRecherche.add(langages);

        panelRecherche.add(new JLabel("Superficie minimume"));
        panelRecherche.add(superficieMin);

        panelRecherche.add(new JLabel("Superficie maximum"));
        panelRecherche.add(superficieMax);

        panelRecherche.add(createXSL);

        add(panelRecherche, BorderLayout.CENTER);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("Interface de recherche de pays");


    }

    public static void main(String ... args) {

        new InterfaceRecherchePays(new File("countries.xml"));

    }

}

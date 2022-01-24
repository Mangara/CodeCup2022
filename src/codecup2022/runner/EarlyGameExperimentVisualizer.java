package codecup2022.runner;

import codecup2022.data.Board;
import codecup2022.data.Move;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

public class EarlyGameExperimentVisualizer {

    public static void main(String[] args) throws IOException {
//        parseAndVisualize(RESULT2);
        parseAndVisualizeDiff(RESULT1, RESULT2);
    }
    
    private static void parseAndVisualize(String results) throws IOException {
        Map<Integer, Double> moveValues = parse(results);

        System.out.println("Move values: ");
        for (Map.Entry<Integer, Double> entry : moveValues.entrySet()) {
            System.out.println(Move.toString(entry.getKey()) + " -> " + entry.getValue());
        }

        double averageScore = computeAverage(moveValues);

        System.out.println("Average: " + averageScore);

        Map<Integer, Double> moveDeltas = computeDeltas(moveValues, averageScore);

        System.out.println("Move deltas: ");
        for (Map.Entry<Integer, Double> entry : moveDeltas.entrySet()) {
            System.out.println(Move.toString(entry.getKey()) + " -> " + entry.getValue());
        }

        visualize(moveDeltas);
    }
    
    private static void parseAndVisualizeDiff(String results1, String results2) throws IOException {
        Map<Integer, Double> moveValues1 = parse(results1);
        Map<Integer, Double> moveValues2 = parse(results2);
        
        // Ensure moveValues1 is larger
        if (moveValues1.size() < moveValues2.size()) {
            Map<Integer, Double> temp = moveValues2;            
            moveValues2 = moveValues1;
            moveValues1 = temp;
        }
        
        Map<Integer, Double> diffValues = new HashMap<>();
        for (Map.Entry<Integer, Double> entry1 : moveValues1.entrySet()) {
            int key = entry1.getKey();
            double value = entry1.getValue();
            Double value2 = moveValues2.get(key);
            
            if (value2 != null) {
                diffValues.put(key, value2 - value);
            }
        }

        System.out.println("Diff values: ");
        for (Map.Entry<Integer, Double> entry : diffValues.entrySet()) {
            System.out.println(Move.toString(entry.getKey()) + " -> " + entry.getValue());
        }

        double averageScore = computeAverage(diffValues);

        System.out.println("Average: " + averageScore);

        Map<Integer, Double> moveDeltas = computeDeltas(diffValues, averageScore);

        System.out.println("Move deltas: ");
        for (Map.Entry<Integer, Double> entry : moveDeltas.entrySet()) {
            System.out.println(Move.toString(entry.getKey()) + " -> " + entry.getValue());
        }

        visualize(moveDeltas);
    }

    private static Map<Integer, Double> parse(String result) {
        Pattern movePattern = Pattern.compile("(\\w\\w\\w) = ([0-9.]+)");
        Matcher matcher = movePattern.matcher(result);

        return matcher.results().collect(
                Collectors.toMap(
                        (match) -> Move.fromString(match.group(1)),
                        (match) -> Double.parseDouble(match.group(2))
                ));
    }

    private static double computeAverage(Map<Integer, Double> moveValues) {
        return moveValues.values().stream().mapToDouble(i -> i).average().getAsDouble();
    }

    private static Map<Integer, Double> computeDeltas(Map<Integer, Double> moveValues, double averageScore) {
        Map<Integer, Double> deltas = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : moveValues.entrySet()) {
            deltas.put(entry.getKey(), entry.getValue() - averageScore);
        }

        return deltas;
    }
    
    private static void visualize(Map<Integer, Double> moveDeltas) throws IOException {
        double minDelta = moveDeltas.values().stream().mapToDouble(i -> i).min().getAsDouble();
        double maxDelta = moveDeltas.values().stream().mapToDouble(i -> i).max().getAsDouble();

        BufferedImage straightImage = visualize(moveDeltas, Board.STRAIGHT, minDelta, maxDelta);
        BufferedImage leftImage = visualize(moveDeltas, Board.LEFT, minDelta, maxDelta);
        BufferedImage rightImage = visualize(moveDeltas, Board.RIGHT, minDelta, maxDelta);

        ImageIO.write(straightImage, "png", Paths.get("EarlyGame-Straight.png").toFile());
        ImageIO.write(leftImage, "png", Paths.get("EarlyGame-Left.png").toFile());
        ImageIO.write(rightImage, "png", Paths.get("EarlyGame-Right.png").toFile());
    }

    public static final int SQUARE_SIZE = 56;
    public static final int OUTER_PADDING = 56;
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 0);
    private static final Color FOREGROUND_COLOR = Color.BLACK;
    private static final Color LIGHT_COLOR = Color.WHITE;
    private static final String DEFAULT_FONT = "Roboto";
    private static Font font = null;

    static {
        // Load the correct font
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        boolean found = false;

        for (Font f : fonts) {
            if (f.getName().equals(DEFAULT_FONT)) {
                font = f.deriveFont(22f);
                found = true;
                break;
            }
        }

        if (!found) {
            System.err.printf("Font \"%s\" was not found.%n", DEFAULT_FONT);
        }
    }

    private static BufferedImage visualize(Map<Integer, Double> moveDeltas, int tile, double minDelta, double maxDelta) {
        int gridWidth = 7;
        int gridHeight = 9;

        int topMargin = OUTER_PADDING;
        int leftMargin = OUTER_PADDING;

        int gridTop = topMargin;
        int gridBottom = gridTop + gridHeight * SQUARE_SIZE;
        int gridLeft = leftMargin;
        int gridRight = gridLeft + gridWidth * SQUARE_SIZE;

        BufferedImage result = new BufferedImage(gridRight + OUTER_PADDING, gridBottom + OUTER_PADDING, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fill background
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, gridRight, gridBottom);
        g.setColor(FOREGROUND_COLOR);

        // Draw top column names
        for (int i = 0; i < gridWidth; i++) {
            double bottom = gridTop - 5;
            double left = gridLeft + i * SQUARE_SIZE;

            String columnName = Character.toString('a' + (char) i);
            TextLayout text = new TextLayout(columnName, font, g.getFontRenderContext());
            double h = text.getBounds().getHeight();
            double w = text.getBounds().getWidth();

            g.drawString(columnName, (int) Math.round(left + (SQUARE_SIZE - w) / 2), (int) Math.round(bottom - (SQUARE_SIZE - h) / 2));
        }

        // Draw side column names
        for (int i = 0; i < gridHeight; i++) {
            double left = gridLeft - 5 - SQUARE_SIZE;
            double bottom = gridTop + (i + 1) * SQUARE_SIZE;

            String columnName = Character.toString('a' + (char) i);
            TextLayout text = new TextLayout(columnName, font, g.getFontRenderContext());
            double h = text.getBounds().getHeight();
            double w = text.getBounds().getWidth();

            g.drawString(columnName, (int) Math.round(left + (SQUARE_SIZE - w) / 2), (int) Math.round(bottom - (SQUARE_SIZE - h) / 2));
        }

        // Fill grid cells
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                int move = Move.fromRowColumnTile(row, col, tile);
                double delta = moveDeltas.getOrDefault(move, 0.0);

                g.setColor(getColor(delta, minDelta, maxDelta));
                g.fillRect(gridLeft + col * SQUARE_SIZE, gridTop + row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        // Draw values in cells
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                int move = Move.fromRowColumnTile(row, col, tile);
                double delta = moveDeltas.getOrDefault(move, 0.0);

                if (delta >= 0) {
                    g.setColor(FOREGROUND_COLOR);
                } else {
                    g.setColor(LIGHT_COLOR);
                }

                double left = gridLeft + col * SQUARE_SIZE;
                double bottom = gridTop + (row + 1) * SQUARE_SIZE;

                String valueText = String.format("%.2f", delta);
                TextLayout text = new TextLayout(valueText, font, g.getFontRenderContext());
                double h = text.getBounds().getHeight();
                double w = text.getBounds().getWidth();

                g.drawString(valueText, (int) Math.round(left + (SQUARE_SIZE - w) / 2), (int) Math.round(bottom - (SQUARE_SIZE - h) / 2));
            }
        }

        // Draw grid lines
        g.setColor(FOREGROUND_COLOR);

        for (int i = 0; i < gridWidth + 1; i++) {
            g.drawLine(gridLeft + i * SQUARE_SIZE, OUTER_PADDING, gridLeft + i * SQUARE_SIZE, gridBottom);
        }

        for (int i = 0; i < gridHeight + 1; i++) {
            g.drawLine(OUTER_PADDING, gridTop + i * SQUARE_SIZE, gridRight, gridTop + i * SQUARE_SIZE);
        }

        return result;
    }

    private static final String RESULT1 = "1/189: aas = 47.016867\n"
            + "2/189: aal = 47.004029\n"
            + "3/189: aar = 46.256588\n"
            + "4/189: abs = 46.795157\n"
            + "5/189: abl = 46.832449\n"
            + "6/189: abr = 46.598037\n"
            + "7/189: acs = 46.712687\n"
            + "8/189: acl = 46.760481\n"
            + "9/189: acr = 46.710041\n"
            + "10/189: ads = 46.700748\n"
            + "11/189: adl = 46.707664\n"
            + "12/189: adr = 46.739971\n"
            + "13/189: aes = 46.729076\n"
            + "14/189: ael = 46.621125\n"
            + "15/189: aer = 46.765750\n"
            + "16/189: afs = 46.822610\n"
            + "17/189: afl = 46.462281\n"
            + "18/189: afr = 46.786242\n"
            + "19/189: ags = 47.007746\n"
            + "20/189: agl = 46.165376\n"
            + "21/189: agr = 46.926083\n"
            + "22/189: bas = 47.098803\n"
            + "23/189: bal = 46.849147\n"
            + "24/189: bar = 46.418208\n"
            + "25/189: bbs = 47.001268\n"
            + "26/189: bbl = 46.769391\n"
            + "27/189: bbr = 46.531582\n"
            + "28/189: bcs = 46.943611\n"
            + "29/189: bcl = 46.677846\n"
            + "30/189: bcr = 46.609554\n"
            + "31/189: bds = 46.904733\n"
            + "32/189: bdl = 46.576843\n"
            + "33/189: bdr = 46.649781\n"
            + "34/189: bes = 46.910497\n"
            + "35/189: bel = 46.486646\n"
            + "36/189: ber = 46.670770\n"
            + "37/189: bfs = 46.963286\n"
            + "38/189: bfl = 46.389651\n"
            + "39/189: bfr = 46.710511\n"
            + "40/189: bgs = 46.991976\n"
            + "41/189: bgl = 46.348438\n"
            + "42/189: bgr = 46.716061\n"
            + "43/189: cas = 47.124998\n"
            + "44/189: cal = 46.753603\n"
            + "45/189: car = 46.534226\n"
            + "46/189: cbs = 47.118958\n"
            + "47/189: cbl = 46.721689\n"
            + "48/189: cbr = 46.537624\n"
            + "49/189: ccs = 47.083797\n"
            + "50/189: ccl = 46.627159\n"
            + "51/189: ccr = 46.567256\n"
            + "52/189: cds = 47.043231\n"
            + "53/189: cdl = 46.533898\n"
            + "54/189: cdr = 46.592495\n"
            + "55/189: ces = 47.025918\n"
            + "56/189: cel = 46.448605\n"
            + "57/189: cer = 46.600756\n"
            + "58/189: cfs = 47.021084\n"
            + "59/189: cfl = 46.398499\n"
            + "60/189: cfr = 46.626477\n"
            + "61/189: cgs = 46.973162\n"
            + "62/189: cgl = 46.462455\n"
            + "63/189: cgr = 46.642925\n"
            + "64/189: das = 47.132939\n"
            + "65/189: dal = 46.712358\n"
            + "66/189: dar = 46.617611\n"
            + "67/189: dbs = 47.171178\n"
            + "68/189: dbl = 46.657596\n"
            + "69/189: dbr = 46.571958\n"
            + "70/189: dcs = 47.148115\n"
            + "71/189: dcl = 46.595232\n"
            + "72/189: dcr = 46.560956\n"
            + "73/189: dds = 47.113994\n"
            + "74/189: ddl = 46.524296\n"
            + "75/189: ddr = 46.557528\n"
            + "76/189: des = 47.077530\n"
            + "77/189: del = 46.453181\n"
            + "78/189: der = 46.547168\n"
            + "79/189: dfs = 47.032534\n"
            + "80/189: dfl = 46.449023\n"
            + "81/189: dfr = 46.557383\n"
            + "82/189: dgs = 46.944487\n"
            + "83/189: dgl = 46.508685\n"
            + "84/189: dgr = 46.594658\n"
            + "85/189: eas = 47.129374\n"
            + "86/189: eal = 46.664473\n"
            + "87/189: ear = 46.667263\n"
            + "88/189: ebs = 47.193949\n"
            + "89/189: ebl = 46.611389\n"
            + "90/189: ebr = 46.602808\n"
            + "91/189: ecs = 47.169194\n"
            + "92/189: ecl = 46.566276\n"
            + "93/189: ecr = 46.564042\n"
            + "94/189: eds = 47.133270\n"
            + "95/189: edl = 46.534076\n"
            + "96/189: edr = 46.526633\n"
            + "97/189: ees = 47.098153\n"
            + "98/189: eel = 46.503666\n"
            + "99/189: eer = 46.510424\n"
            + "100/189: efs = 47.043220\n"
            + "101/189: efl = 46.498772\n"
            + "102/189: efr = 46.498736\n"
            + "103/189: egs = 46.932530\n"
            + "104/189: egl = 46.560826\n"
            + "105/189: egr = 46.553325\n"
            + "106/189: fas = 47.137437\n"
            + "107/189: fal = 46.609720\n"
            + "108/189: far = 46.702876\n"
            + "109/189: fbs = 47.181295\n"
            + "110/189: fbl = 46.563210\n"
            + "111/189: fbr = 46.657608\n"
            + "112/189: fcs = 47.144571\n"
            + "113/189: fcl = 46.561392\n"
            + "114/189: fcr = 46.590463\n"
            + "115/189: fds = 47.119574\n"
            + "116/189: fdl = 46.557334\n"
            + "117/189: fdr = 46.520570\n"
            + "118/189: fes = 47.072048\n"
            + "119/189: fel = 46.559141\n"
            + "120/189: fer = 46.464661\n"
            + "121/189: ffs = 47.038710\n"
            + "122/189: ffl = 46.559954\n"
            + "123/189: ffr = 46.459274\n"
            + "124/189: fgs = 46.942855\n"
            + "125/189: fgl = 46.589995\n"
            + "126/189: fgr = 46.517199\n"
            + "127/189: gas = 47.132749\n"
            + "128/189: gal = 46.540096\n"
            + "129/189: gar = 46.763293\n"
            + "130/189: gbs = 47.122324\n"
            + "131/189: gbl = 46.538418\n"
            + "132/189: gbr = 46.712276\n"
            + "133/189: gcs = 47.075171\n"
            + "134/189: gcl = 46.578619\n"
            + "135/189: gcr = 46.627142\n"
            + "136/189: gds = 47.051378\n"
            + "137/189: gdl = 46.587077\n"
            + "138/189: gdr = 46.534432\n"
            + "139/189: ges = 47.026380\n"
            + "140/189: gel = 46.606450\n"
            + "141/189: ger = 46.453141\n"
            + "142/189: gfs = 47.021288\n"
            + "143/189: gfl = 46.631008\n"
            + "144/189: gfr = 46.392284\n"
            + "145/189: ggs = 46.972518\n"
            + "146/189: ggl = 46.635713\n"
            + "147/189: ggr = 46.456366\n"
            + "148/189: has = 47.103952\n"
            + "149/189: hal = 46.416103\n"
            + "150/189: har = 46.837260\n"
            + "151/189: hbs = 47.009530\n"
            + "152/189: hbl = 46.519372\n"
            + "153/189: hbr = 46.772721\n"
            + "154/189: hcs = 46.941843\n"
            + "155/189: hcl = 46.610942\n"
            + "156/189: hcr = 46.680792\n"
            + "157/189: hds = 46.918603\n"
            + "158/189: hdl = 46.645290\n"
            + "159/189: hdr = 46.583830\n"
            + "160/189: hes = 46.927284\n"
            + "161/189: hel = 46.673566\n"
            + "162/189: her = 46.480107\n"
            + "163/189: hfs = 46.962890\n"
            + "164/189: hfl = 46.703046\n"
            + "165/189: hfr = 46.387721\n"
            + "166/189: hgs = 47.000865\n"
            + "167/189: hgl = 46.714084\n"
            + "168/189: hgr = 46.370489\n"
            + "169/189: ias = 47.026978\n"
            + "170/189: ial = 46.256294\n"
            + "171/189: iar = 47.003320\n"
            + "172/189: ibs = 46.797388\n"
            + "173/189: ibl = 46.595889\n"
            + "174/189: ibr = 46.830460\n"
            + "175/189: ics = 46.715398\n"
            + "176/189: icl = 46.708002\n"
            + "177/189: icr = 46.772661\n"
            + "178/189: ids = 46.693411\n"
            + "179/189: idl = 46.740795\n"
            + "180/189: idr = 46.701600\n"
            + "181/189: ies = 46.723227\n"
            + "182/189: iel = 46.751885\n"
            + "183/189: ier = 46.605154\n"
            + "184/189: ifs = 46.810471\n"
            + "185/189: ifl = 46.795645\n"
            + "186/189: ifr = 46.464280\n"
            + "187/189: igs = 47.013090\n"
            + "188/189: igl = 46.931115\n"
            + "189/189: igr = 46.155246";
    
    private static final String RESULT2 = "1/183: aas = 47.199511\n" +
"2/183: aal = 47.182553\n" +
"3/183: aar = 46.443245\n" +
"4/183: abs = 46.946382\n" +
"5/183: abl = 46.998495\n" +
"6/183: abr = 46.828011\n" +
"7/183: acs = 46.848397\n" +
"8/183: acl = 46.918813\n" +
"9/183: acr = 46.924199\n" +
"10/183: ads = 46.827306\n" +
"11/183: adl = 46.880881\n" +
"12/183: adr = 46.950865\n" +
"13/183: aes = 46.875827\n" +
"14/183: ael = 46.790809\n" +
"15/183: aer = 46.939768\n" +
"16/183: afs = 46.985944\n" +
"17/183: afl = 46.640448\n" +
"18/183: afr = 46.958740\n" +
"19/183: ags = 47.182296\n" +
"20/183: agl = 46.330683\n" +
"21/183: agr = 47.111331\n" +
"22/183: bas = 47.318653\n" +
"23/183: bal = 46.997108\n" +
"24/183: bar = 46.603512\n" +
"25/183: bbs = 47.213137\n" +
"26/183: bbl = 46.886952\n" +
"27/183: bbr = 46.766670\n" +
"28/183: bcs = 47.076825\n" +
"29/183: bcl = 46.820115\n" +
"30/183: bcr = 46.869657\n" +
"31/183: bds = 46.926509\n" +
"32/183: bdl = 46.796715\n" +
"33/183: bdr = 46.928780\n" +
"34/183: bes = 47.043900\n" +
"35/183: bel = 46.714122\n" +
"36/183: ber = 46.843805\n" +
"37/183: bfs = 47.124159\n" +
"38/183: bfl = 46.550092\n" +
"39/183: bfr = 46.878242\n" +
"40/183: bgs = 47.177321\n" +
"41/183: bgl = 46.531898\n" +
"42/183: bgr = 46.907264\n" +
"43/183: cas = 47.392375\n" +
"44/183: cal = 46.830020\n" +
"45/183: car = 46.772484\n" +
"46/183: cbs = 47.345163\n" +
"47/183: cbl = 46.884502\n" +
"48/183: cbr = 46.696488\n" +
"49/183: ccs = 47.289948\n" +
"50/183: ccl = 46.688062\n" +
"51/183: ccr = 46.820459\n" +
"52/183: ces = 47.238667\n" +
"53/183: cel = 46.572130\n" +
"54/183: cer = 46.786944\n" +
"55/183: cfs = 47.194964\n" +
"56/183: cfl = 46.574502\n" +
"57/183: cfr = 46.789204\n" +
"58/183: cgs = 47.142520\n" +
"59/183: cgl = 46.631325\n" +
"60/183: cgr = 46.815200\n" +
"61/183: das = 47.444426\n" +
"62/183: dal = 47.098588\n" +
"63/183: dar = 46.494417\n" +
"64/183: dbs = 47.420108\n" +
"65/183: dbl = 46.205633\n" +
"66/183: dbr = 47.312319\n" +
"67/183: dcs = 47.226912\n" +
"68/183: dcl = 47.014917\n" +
"69/183: dcr = 46.585568\n" +
"70/183: dds = 47.124060\n" +
"71/183: ddl = 46.764785\n" +
"72/183: ddr = 46.789722\n" +
"73/183: des = 47.201024\n" +
"74/183: del = 46.617252\n" +
"75/183: der = 46.790303\n" +
"76/183: dfs = 47.210864\n" +
"77/183: dfl = 46.630979\n" +
"78/183: dfr = 46.733504\n" +
"79/183: dgs = 47.123742\n" +
"80/183: dgl = 46.696475\n" +
"81/183: dgr = 46.784463\n" +
"82/183: eas = 47.294636\n" +
"83/183: eal = 46.807612\n" +
"84/183: ear = 46.908136\n" +
"85/183: ecs = 47.283892\n" +
"86/183: ecl = 46.727909\n" +
"87/183: ecr = 46.840021\n" +
"88/183: eds = 47.274566\n" +
"89/183: edl = 46.738869\n" +
"90/183: edr = 46.716271\n" +
"91/183: ees = 47.243961\n" +
"92/183: eel = 46.675073\n" +
"93/183: eer = 46.694710\n" +
"94/183: efs = 47.225083\n" +
"95/183: efl = 46.667660\n" +
"96/183: efr = 46.674123\n" +
"97/183: egs = 47.105169\n" +
"98/183: egl = 46.750404\n" +
"99/183: egr = 46.733126\n" +
"100/183: fas = 47.249134\n" +
"101/183: fal = 46.989097\n" +
"102/183: far = 46.739193\n" +
"103/183: fbs = 47.423457\n" +
"104/183: fbl = 46.147493\n" +
"105/183: fbr = 47.368235\n" +
"106/183: fcs = 47.434974\n" +
"107/183: fcl = 46.962881\n" +
"108/183: fcr = 46.442463\n" +
"109/183: fds = 47.302964\n" +
"110/183: fdl = 46.747002\n" +
"111/183: fdr = 46.688108\n" +
"112/183: fes = 47.247077\n" +
"113/183: fel = 46.734029\n" +
"114/183: fer = 46.646495\n" +
"115/183: ffs = 47.219793\n" +
"116/183: ffl = 46.730422\n" +
"117/183: ffr = 46.630091\n" +
"118/183: fgs = 47.110100\n" +
"119/183: fgl = 46.764695\n" +
"120/183: fgr = 46.691986\n" +
"121/183: gas = 47.260258\n" +
"122/183: gal = 46.631637\n" +
"123/183: gar = 47.064583\n" +
"124/183: gbs = 47.320805\n" +
"125/183: gbl = 46.723554\n" +
"126/183: gbr = 46.854976\n" +
"127/183: gcs = 47.282062\n" +
"128/183: gcl = 46.647821\n" +
"129/183: gcr = 46.887865\n" +
"130/183: gds = 47.225373\n" +
"131/183: gdl = 46.804394\n" +
"132/183: gdr = 46.645515\n" +
"133/183: ges = 47.212484\n" +
"134/183: gel = 46.789000\n" +
"135/183: ger = 46.635962\n" +
"136/183: gfs = 47.200522\n" +
"137/183: gfl = 46.800335\n" +
"138/183: gfr = 46.580638\n" +
"139/183: ggs = 47.155011\n" +
"140/183: ggl = 46.823315\n" +
"141/183: ggr = 46.629271\n" +
"142/183: has = 47.271934\n" +
"143/183: hal = 46.584437\n" +
"144/183: har = 47.028511\n" +
"145/183: hbs = 47.193881\n" +
"146/183: hbl = 46.648112\n" +
"147/183: hbr = 47.002968\n" +
"148/183: hcs = 47.121970\n" +
"149/183: hcl = 46.794159\n" +
"150/183: hcr = 46.844411\n" +
"151/183: hds = 47.100522\n" +
"152/183: hdl = 46.813425\n" +
"153/183: hdr = 46.771263\n" +
"154/183: hes = 47.103928\n" +
"155/183: hel = 46.853515\n" +
"156/183: her = 46.636873\n" +
"157/183: hfs = 47.148942\n" +
"158/183: hfl = 46.878052\n" +
"159/183: hfr = 46.560970\n" +
"160/183: hgs = 47.178953\n" +
"161/183: hgl = 46.899159\n" +
"162/183: hgr = 46.539429\n" +
"163/183: ias = 47.204740\n" +
"164/183: ial = 46.404623\n" +
"165/183: iar = 47.198608\n" +
"166/183: ibs = 46.991329\n" +
"167/183: ibl = 46.758744\n" +
"168/183: ibr = 47.020047\n" +
"169/183: ics = 46.890032\n" +
"170/183: icl = 46.867682\n" +
"171/183: icr = 46.959431\n" +
"172/183: ids = 46.877682\n" +
"173/183: idl = 46.927395\n" +
"174/183: idr = 46.880168\n" +
"175/183: ies = 46.914133\n" +
"176/183: iel = 46.926430\n" +
"177/183: ier = 46.780022\n" +
"178/183: ifs = 46.993811\n" +
"179/183: ifl = 46.968343\n" +
"180/183: ifr = 46.635384\n" +
"181/183: igs = 47.191661\n" +
"182/183: igl = 47.105691\n" +
"183/183: igr = 46.326448";

    public static Color getColor(double value, double minValue, double maxValue) {
        double normalizedValue = (value - minValue) / (maxValue - minValue);

        int index = (int) Math.floor(normalizedValue * COLOR_MAP.length);

        if (index < 0) {
            index = 0;
        } else if (index >= COLOR_MAP.length) {
            index = COLOR_MAP.length - 1;
        }

        return COLOR_MAP[index];
    }

    private static final Color[] COLOR_MAP = new Color[]{
        new Color(26, 0, 134),
        new Color(26, 0, 136),
        new Color(26, 0, 137),
        new Color(26, 0, 139),
        new Color(26, 1, 141),
        new Color(26, 1, 143),
        new Color(26, 2, 145),
        new Color(26, 3, 146),
        new Color(26, 4, 148),
        new Color(26, 4, 150),
        new Color(27, 5, 152),
        new Color(27, 6, 153),
        new Color(27, 7, 155),
        new Color(27, 8, 157),
        new Color(27, 9, 158),
        new Color(27, 10, 160),
        new Color(27, 11, 162),
        new Color(27, 12, 163),
        new Color(27, 13, 165),
        new Color(27, 14, 167),
        new Color(27, 15, 168),
        new Color(27, 16, 170),
        new Color(27, 17, 172),
        new Color(27, 18, 173),
        new Color(27, 19, 175),
        new Color(27, 21, 176),
        new Color(27, 22, 178),
        new Color(27, 23, 180),
        new Color(27, 24, 181),
        new Color(27, 25, 183),
        new Color(27, 26, 184),
        new Color(27, 27, 186),
        new Color(27, 28, 187),
        new Color(27, 29, 188),
        new Color(27, 30, 190),
        new Color(27, 31, 191),
        new Color(27, 32, 193),
        new Color(27, 33, 194),
        new Color(27, 34, 195),
        new Color(27, 35, 197),
        new Color(27, 36, 198),
        new Color(27, 37, 199),
        new Color(27, 39, 200),
        new Color(27, 40, 201),
        new Color(28, 41, 203),
        new Color(28, 42, 204),
        new Color(28, 43, 205),
        new Color(28, 44, 206),
        new Color(28, 45, 207),
        new Color(28, 47, 208),
        new Color(28, 48, 209),
        new Color(29, 49, 210),
        new Color(29, 50, 210),
        new Color(29, 52, 211),
        new Color(29, 53, 212),
        new Color(29, 54, 213),
        new Color(30, 55, 213),
        new Color(30, 57, 214),
        new Color(30, 58, 214),
        new Color(30, 59, 215),
        new Color(31, 61, 215),
        new Color(31, 62, 215),
        new Color(31, 64, 215),
        new Color(31, 65, 215),
        new Color(32, 67, 215),
        new Color(32, 68, 215),
        new Color(32, 70, 215),
        new Color(32, 71, 214),
        new Color(33, 73, 214),
        new Color(33, 75, 213),
        new Color(33, 76, 212),
        new Color(33, 78, 211),
        new Color(33, 80, 209),
        new Color(33, 82, 207),
        new Color(33, 84, 205),
        new Color(32, 86, 202),
        new Color(32, 88, 199),
        new Color(32, 90, 196),
        new Color(32, 92, 193),
        new Color(32, 94, 190),
        new Color(32, 96, 187),
        new Color(33, 98, 184),
        new Color(33, 100, 181),
        new Color(34, 101, 178),
        new Color(35, 103, 175),
        new Color(36, 105, 173),
        new Color(37, 106, 170),
        new Color(38, 108, 167),
        new Color(39, 109, 164),
        new Color(40, 111, 161),
        new Color(41, 112, 159),
        new Color(42, 113, 156),
        new Color(43, 115, 153),
        new Color(44, 116, 150),
        new Color(45, 118, 148),
        new Color(46, 119, 145),
        new Color(47, 120, 142),
        new Color(47, 121, 140),
        new Color(48, 123, 137),
        new Color(49, 124, 134),
        new Color(50, 125, 132),
        new Color(51, 126, 129),
        new Color(52, 128, 126),
        new Color(53, 129, 124),
        new Color(54, 130, 121),
        new Color(55, 131, 118),
        new Color(55, 132, 116),
        new Color(56, 134, 113),
        new Color(57, 135, 110),
        new Color(58, 136, 108),
        new Color(59, 137, 105),
        new Color(59, 138, 102),
        new Color(60, 139, 100),
        new Color(61, 140, 97),
        new Color(62, 142, 94),
        new Color(62, 143, 92),
        new Color(63, 144, 89),
        new Color(64, 145, 87),
        new Color(65, 146, 85),
        new Color(66, 147, 82),
        new Color(67, 148, 80),
        new Color(68, 149, 78),
        new Color(68, 150, 76),
        new Color(69, 151, 74),
        new Color(71, 152, 72),
        new Color(72, 153, 70),
        new Color(73, 154, 68),
        new Color(74, 155, 66),
        new Color(75, 156, 65),
        new Color(76, 157, 63),
        new Color(77, 158, 61),
        new Color(79, 159, 60),
        new Color(80, 160, 58),
        new Color(81, 161, 56),
        new Color(83, 161, 55),
        new Color(84, 162, 53),
        new Color(86, 163, 52),
        new Color(87, 164, 50),
        new Color(89, 165, 49),
        new Color(90, 166, 47),
        new Color(92, 167, 46),
        new Color(93, 168, 44),
        new Color(95, 168, 43),
        new Color(97, 169, 42),
        new Color(98, 170, 40),
        new Color(100, 171, 39),
        new Color(102, 172, 38),
        new Color(104, 173, 37),
        new Color(105, 173, 36),
        new Color(107, 174, 34),
        new Color(109, 175, 33),
        new Color(111, 176, 32),
        new Color(113, 177, 31),
        new Color(115, 177, 30),
        new Color(116, 178, 29),
        new Color(118, 179, 28),
        new Color(120, 180, 27),
        new Color(122, 180, 27),
        new Color(124, 181, 26),
        new Color(126, 182, 25),
        new Color(128, 183, 25),
        new Color(130, 183, 24),
        new Color(132, 184, 23),
        new Color(134, 185, 23),
        new Color(136, 186, 23),
        new Color(139, 186, 22),
        new Color(141, 187, 22),
        new Color(143, 188, 22),
        new Color(145, 188, 22),
        new Color(147, 189, 22),
        new Color(149, 190, 22),
        new Color(151, 190, 22),
        new Color(154, 191, 23),
        new Color(156, 192, 23),
        new Color(158, 192, 23),
        new Color(160, 193, 24),
        new Color(162, 194, 24),
        new Color(164, 194, 25),
        new Color(166, 195, 25),
        new Color(169, 196, 26),
        new Color(171, 196, 26),
        new Color(173, 197, 27),
        new Color(175, 198, 28),
        new Color(177, 198, 28),
        new Color(179, 199, 29),
        new Color(181, 200, 30),
        new Color(183, 200, 31),
        new Color(185, 201, 32),
        new Color(187, 202, 33),
        new Color(189, 202, 33),
        new Color(191, 203, 34),
        new Color(192, 204, 35),
        new Color(194, 204, 36),
        new Color(196, 205, 37),
        new Color(198, 206, 38),
        new Color(200, 206, 40),
        new Color(202, 207, 41),
        new Color(203, 208, 42),
        new Color(205, 208, 43),
        new Color(207, 209, 44),
        new Color(209, 210, 46),
        new Color(210, 210, 47),
        new Color(212, 211, 48),
        new Color(214, 212, 50),
        new Color(215, 213, 51),
        new Color(217, 213, 53),
        new Color(219, 214, 54),
        new Color(220, 215, 56),
        new Color(222, 216, 57),
        new Color(223, 216, 59),
        new Color(224, 217, 61),
        new Color(226, 218, 63),
        new Color(227, 219, 64),
        new Color(229, 220, 66),
        new Color(230, 220, 68),
        new Color(231, 221, 70),
        new Color(232, 222, 72),
        new Color(233, 223, 74),
        new Color(234, 224, 77),
        new Color(235, 225, 79),
        new Color(236, 226, 82),
        new Color(237, 227, 84),
        new Color(238, 228, 87),
        new Color(238, 229, 90),
        new Color(239, 230, 93),
        new Color(239, 231, 96),
        new Color(239, 232, 99),
        new Color(240, 233, 102),
        new Color(240, 234, 106),
        new Color(240, 235, 110),
        new Color(241, 236, 114),
        new Color(241, 237, 118),
        new Color(242, 238, 122),
        new Color(242, 239, 126),
        new Color(243, 240, 131),
        new Color(243, 241, 135),
        new Color(244, 242, 140),
        new Color(245, 243, 145),
        new Color(245, 244, 150),
        new Color(246, 244, 155),
        new Color(247, 245, 160),
        new Color(247, 246, 165),
        new Color(248, 247, 171),
        new Color(249, 247, 176),
        new Color(249, 248, 182),
        new Color(250, 249, 188),
        new Color(251, 250, 194),
        new Color(251, 250, 200),
        new Color(252, 251, 207),
        new Color(252, 252, 213),
        new Color(253, 252, 220),
        new Color(253, 253, 226),
        new Color(254, 253, 233),
        new Color(254, 254, 240),
        new Color(255, 254, 248),
        new Color(255, 255, 255)
    };
}

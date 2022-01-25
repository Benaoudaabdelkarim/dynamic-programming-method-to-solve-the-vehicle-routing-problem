/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrpapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author LordAbdElKarim
 */
public class Vrpapp extends Application {
    
    

    /**
     * @param args the command line arguments
     */
    
    VBox vbox;
    TextField nbrClientsField = new TextField();
    Button setClients = new Button("ajoute clients");
    ComboBox<String> nbrVehiculeField = new ComboBox<>();
    Button setVehicule = new Button("ajoute vehicules");
    TextField depotX = new TextField();
    TextField depotY = new TextField();
    Button setDepot = new Button("ajoute depot");
    Button clear = new Button("clear");
    Button optimisation = new Button("optimisation");
    Button co2 = new Button("CO2");
    static Label co2Label =new Label();


    static int nbrClients=40;
    double[] X=new double[nbrClients];
    double[] Y=new double[nbrClients];
    static ArrayList<Circle> pointCloud = new ArrayList<>();
    static ArrayList<Line>  Paths_1 = new ArrayList<>();
    static ArrayList<Line> Paths_2 = new ArrayList<>();
    static ArrayList<Line> Paths_3 = new ArrayList<>();
    static ArrayList<Line> Paths_4 = new ArrayList<>();
    static ArrayList<Point2D> points=new ArrayList<>();
    static ArrayList<Double> distances=new ArrayList<Double>();
    static ArrayList<Number> l=new ArrayList<>();
    static Point2D tempp;

    static int nextPosition;
    static int curentPosition=0;
    static ArrayList<Point2D> temp=new ArrayList<>();
    static ArrayList<Point2D> pointsRightTop=new ArrayList<>();
    static ArrayList<Point2D> pointsLeftTop=new ArrayList<>();
    static ArrayList<Point2D> pointsRightBottom=new ArrayList<>();
    static ArrayList<Point2D> pointsLeftBottom=new ArrayList<>();

    static ArrayList<Point2D> pointsRight=new ArrayList<>();
    static ArrayList<Point2D> pointsLeft=new ArrayList<>();

    Circle circle;
    Circle circleDepot;
    static Line line ;
    Point2D point;
    int width=1200;
    int height=600;
    int nbrV;
    Group root= new Group();
    Scene scene=new Scene(root, width, height);
    double x=200, y=30;
    static double[][] distM ;
    static Dptsp solver;
    static double coast=0;
    static Double e, d;;


    @Override
    public void start(Stage primaryStage) {
        //ajouter les components
        nbrClientsField.setText("10");
        vbox = new VBox(20);
        vbox.setAlignment(Pos.BASELINE_CENTER);
        vbox.prefHeightProperty().bind(scene.heightProperty().multiply(1.00));
        vbox.prefWidthProperty().bind(scene.widthProperty().multiply(0.12));
        vbox.setStyle("-fx-background-color:#ababab; -fx-margin-bottom:0px");
        depotX.setText("600");depotY.setText("300");
        vbox.getChildren().add(depotX);vbox.getChildren().add(depotY);
        vbox.getChildren().add(setDepot);setDepot.setStyle("-fx-pref-width:140px;");
        vbox.getChildren().add(nbrClientsField);
        nbrClientsField.setStyle("-fx-width:140px;");
        nbrVehiculeField.setPromptText("nombre de véhicule");
        nbrVehiculeField.getItems().addAll("1", "2", "4");
        vbox.getChildren().add(setClients);
        setClients.setStyle("-fx-pref-width:140px;");
        vbox.getChildren().add(nbrVehiculeField);
        vbox.getChildren().add(setVehicule);
        setVehicule.setStyle("-fx-pref-width:140px;");
        vbox.getChildren().add(optimisation);optimisation.setStyle("-fx-pref-width:140px;");
        vbox.getChildren().add(clear);clear.setStyle("-fx-pref-width:140px;");
        vbox.getChildren().add(co2Label);

        setDepot.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //Depot
                root.getChildren().remove(circleDepot);
                root.getChildren().removeAll(pointCloud);
                root.getChildren().removeAll(Paths_1);
                root.getChildren().removeAll(Paths_2);
                root.getChildren().removeAll(Paths_3);
                root.getChildren().removeAll(Paths_4);
                circleDepot=new Circle(Double.parseDouble(depotX.getText()),Double.parseDouble(depotY.getText()),15, Color.AQUA);
                root.getChildren().add(circleDepot);
            }
        });

        setClients.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                root.getChildren().remove(circleDepot);
                root.getChildren().removeAll(pointCloud);
                root.getChildren().removeAll(Paths_1);
                root.getChildren().removeAll(Paths_2);
                root.getChildren().removeAll(Paths_3);
                root.getChildren().removeAll(Paths_4);
                pointCloud.clear();
                points.clear();

                //set number of clients
                nbrClients = Integer.parseInt(nbrClientsField.getText());
                //set x's and y's
                X=randomArray(width, "x");
                Y=randomArray(height, "y");

                //depot
                pointCloud.add(circleDepot);

                //Clients
                for(int i=0;i<nbrClients; i++){
                    circle=new Circle(X[i],Y[i],10);
                    pointCloud.add(circle);
                }

                //add clients
                for(int i=0;i<=nbrClients; i++){
                    point=new Point2D(pointCloud.get(i).getCenterX(),pointCloud.get(i).getCenterY());
                    points.add(point);
                }
                //scene
                root.getChildren().addAll(pointCloud);
            }
        });

        setVehicule.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                root.getChildren().removeAll(Paths_1);
                root.getChildren().removeAll(Paths_2);
                root.getChildren().removeAll(Paths_3);
                root.getChildren().removeAll(Paths_4);
                pointsLeftTop.clear();
                pointsRightTop.clear();
                pointsRightBottom.clear();
                pointsLeftBottom.clear();
                pointsRight.clear();
                pointsLeft.clear();
                Paths_1.clear();
                Paths_2.clear();
                Paths_3.clear();
                Paths_4.clear();
                nbrV= Integer.parseInt(nbrVehiculeField.getValue());
                if (nbrV == 4){
                    filter(nbrV);
                    root.getChildren().addAll(pathslist(pointsLeftTop, Paths_1,Color.BLACK));
                    root.getChildren().addAll(pathslist(pointsRightTop, Paths_2,Color.ROYALBLUE));
                    root.getChildren().addAll(pathslist(pointsRightBottom, Paths_3,Color.RED));
                    root.getChildren().addAll(pathslist(pointsLeftBottom, Paths_4,Color.CHARTREUSE));
                }
                else if (nbrV == 2){
                    filter(nbrV);
                    root.getChildren().addAll(pathslist(pointsLeft, Paths_3,Color.BLACK));
                    root.getChildren().addAll(pathslist(pointsRight, Paths_4,Color.ROYALBLUE));
                }
                else if (nbrV == 1){
                    temp.add(points.get(0));
                    root.getChildren().addAll(pathslist(points, Paths_1,Color.BLACK));
                }
            }
        });

        optimisation.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                root.getChildren().removeAll(Paths_1);
                root.getChildren().removeAll(Paths_2);
                root.getChildren().removeAll(Paths_3);
                root.getChildren().removeAll(Paths_4);
                Paths_1.clear();
                Paths_2.clear();
                Paths_3.clear();
                Paths_4.clear();
                coast=0;
                if (nbrV == 4){
                    root.getChildren().addAll(optemisedPath(pointsLeftTop, Paths_1,Color.BLACK));
                    root.getChildren().addAll(optemisedPath(pointsRightTop, Paths_2,Color.ROYALBLUE));
                    root.getChildren().addAll(optemisedPath(pointsRightBottom, Paths_3,Color.RED));
                    root.getChildren().addAll(optemisedPath(pointsLeftBottom, Paths_4,Color.CHARTREUSE));
                }
                else if (nbrV == 2){
                    root.getChildren().addAll(optemisedPath(pointsLeft, Paths_1,Color.BLACK));
                    root.getChildren().addAll(optemisedPath(pointsRight, Paths_2,Color.ROYALBLUE));
                }
                else if (nbrV == 1){
                    root.getChildren().addAll(optemisedPath(points, Paths_1,Color.BLACK));
                }
                Co2Calculation();
            }
        });

        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                root.getChildren().remove(circleDepot);
                root.getChildren().removeAll(pointCloud);
                root.getChildren().removeAll(Paths_1);
                root.getChildren().removeAll(Paths_2);
                root.getChildren().removeAll(Paths_3);
                root.getChildren().removeAll(Paths_4);
                pointCloud.clear();
                points.clear();
                pointsLeftTop.clear();
                pointsRightTop.clear();
                pointsRightBottom.clear();
                pointsLeftBottom.clear();
                Paths_1.clear();
                Paths_2.clear();
                Paths_3.clear();
                Paths_4.clear();
            }
        });

        //scene
        root.getChildren().addAll(vbox);

        primaryStage.setTitle("VRP");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static double[] randomArray(int b, String s){
        Random random= new Random();
        double[] A=new double[nbrClients];

        if(s.equals("y"))
            for(int i=0; i<nbrClients; i++)
                A[i] = Math.round(0 + (b) * random.nextDouble() * 1);
        else
            for(int i=0; i<nbrClients; i++)
                A[i] = Math.round(180 + (b-180) * random.nextDouble() * 1);


        return A;

    }
    public static void printTable(double[] A){
        for(int i=0; i<nbrClients; i++){
            System.out.print(A[i]+"\t");
        }
    }
    public static int mindistance(ArrayList<Double> A){
        double a=Double.POSITIVE_INFINITY;
        int index=0;

        for(int i=0; i<A.size(); i++){
            if(A.get(i) !=0)
                if(a> A.get(i)){
                    a= A.get(i);
                    index=i;
                }

        }
        l.add(index);
        l.add(A.get(index));
        return index;
    }

    public static ArrayList<Line> pathslist(ArrayList<Point2D> pointList, ArrayList<Line> path, Color color){
        temp.clear();distances.clear();curentPosition=0;temp.add(points.get(0));
        while(path.size()!=pointList.size()){
            distances.clear();
            for(int i=0; i<pointList.size(); i++){
                if(temp.contains(pointList.get(i))) distances.add(Double.POSITIVE_INFINITY);
                else
                    distances.add(pointList.get(curentPosition).distance(pointList.get(i)));
            }
            l.clear();
            nextPosition=mindistance(distances);

            line =new Line(pointList.get(curentPosition).getX(),pointList.get(curentPosition).getY(),pointList.get(nextPosition).getX(),pointList.get(nextPosition).getY());
            line.setStroke(color);
            path.add(line);

            curentPosition=nextPosition;
            tempp=new Point2D(pointList.get(nextPosition).getX(),pointList.get(nextPosition).getY());
            temp.add(tempp);
        }
        return path;
    }
    public static ArrayList<Line> optemisedPath(ArrayList<Point2D> pointList, ArrayList<Line> path, Color color){
        ArrayList<Integer> indexes = ditanceMatrix(pointList);
        for(int i=0; i<indexes.size()-1; i++){
            line =new Line(pointList.get(indexes.get(i)).getX(),pointList.get(indexes.get(i)).getY(),
                    pointList.get(indexes.get(i+1)).getX(),pointList.get(indexes.get(i+1)).getY());
            line.setStroke(color);
            path.add(line);
        }
        return path;
    }
    public static ArrayList<Integer> ditanceMatrix(ArrayList<Point2D> pointList) {
        int pls = pointList.size();
        distM = new double[pls][pls];
        for (int i = 0; i < pls; i++) {
            for (int j = 0; j < pls; j++) {
                if (i == j) distM[i][j] = Double.POSITIVE_INFINITY;
                else {
                    distM[i][j]=distanceFunction(pointList.get(i).getX(), pointList.get(j).getX(),pointList.get(i).getY(), pointList.get(j).getY());
                }
            }
        }
        solver = new Dptsp(0, distM);
        System.out.println("tour : "+solver.getTour()+"\t"+solver.getTourCost());
        coast+=solver.getTourCost();
        co2Label.setText("essence: "+e+"\n"+"diesel: "+d);
        return solver.getTour();
    }
    public static double distanceFunction(double x1, double x2, double y1, double y2){
        return Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
    }
    public static void filter(int v){
        if (v==4){
            temp.add(points.get(0));
            pointsRightTop.add(points.get(0));
            pointsLeftTop.add(points.get(0));
            pointsRightBottom.add(points.get(0));
            pointsLeftBottom.add(points.get(0));

            for(int i=1; i<points.size(); i++){
                if(points.get(i).getX()<points.get(0).getX() && points.get(i).getY()<points.get(0).getY())
                    pointsLeftTop.add(points.get(i));
                if(points.get(i).getX()>points.get(0).getX() && points.get(i).getY()<points.get(0).getY())
                    pointsRightTop.add(points.get(i));
                if(points.get(i).getX()<points.get(0).getX() && points.get(i).getY()>points.get(0).getY())
                    pointsLeftBottom.add(points.get(i));
                if(points.get(i).getX()>points.get(0).getX() && points.get(i).getY()>points.get(0).getY())
                    pointsRightBottom.add(points.get(i));
            }
        }else if(v==2){
            temp.add(points.get(0));
            pointsRight.add(points.get(0));
            pointsLeft.add(points.get(0));

            for(int i=1; i<points.size(); i++){
                if(points.get(i).getX()<points.get(0).getX())
                    pointsRight.add(points.get(i));
                if(points.get(i).getX()>points.get(0).getX())
                    pointsLeft.add(points.get(i));
            }
        }
    }
    public static void Co2Calculation(){
        d = coast*5/100;
        d=d*2640;

        e = coast*5/100;
        e=e*2392;

        System.out.println("diesel: "+d);
        System.out.println("essence: "+e);
    }

    
}

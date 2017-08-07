package com.roszczyk.ledcalculator;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Controller {

    private static final int R = 0;
    private static final int G = 1;
    private static final int B = 2;

    @FXML
    private TextField inputLevelsTF;
    @FXML
    private TextField gammaRTF;
    @FXML
    private TextField outputLevelRTF;
    @FXML
    private TextField gammaGTF;
    @FXML
    private TextField outputLevelGTF;
    @FXML
    private TextField gammaBTF;
    @FXML
    private TextField outputLevelBTF;
    @FXML
    private Button calculateBT;
    @FXML
    private LineChart<String, Number> outputChart;
    @FXML
    private TextArea dataTableTA;
    private XYChart.Series<String, Number> rSeries = new XYChart.Series<>();
    private XYChart.Series<String, Number> gSeries = new XYChart.Series<>();
    private XYChart.Series<String, Number> bSeries = new XYChart.Series<>();
    private XYChart.Series<String, Number> linearSeries = new XYChart.Series<>();

    private IntegerProperty inputLevels = new SimpleIntegerProperty( 15 );
    private DoubleProperty gamma[] = {new SimpleDoubleProperty( 1.8 ), new SimpleDoubleProperty( 1.8 ), new SimpleDoubleProperty( 1.8 )};
    private IntegerProperty maxOutputLevel[] = {new SimpleIntegerProperty( 255 ), new SimpleIntegerProperty( 255 ), new SimpleIntegerProperty( 255 )};

    public Controller() {
    }

    public void initialize() {
        inputLevelsTF.textProperty().bindBidirectional( inputLevels, new NumberStringConverter() );
        gammaRTF.textProperty().bindBidirectional( gamma[R], new NumberStringConverter() );
        gammaGTF.textProperty().bindBidirectional( gamma[G], new NumberStringConverter() );
        gammaBTF.textProperty().bindBidirectional( gamma[B], new NumberStringConverter() );
        outputLevelRTF.textProperty().bindBidirectional( maxOutputLevel[R], new NumberStringConverter() );
        outputLevelGTF.textProperty().bindBidirectional( maxOutputLevel[G], new NumberStringConverter() );
        outputLevelBTF.textProperty().bindBidirectional( maxOutputLevel[B], new NumberStringConverter() );
        calculateBT.setOnAction( this::handleCalculate );
        outputChart.getData().add( rSeries );
        outputChart.getData().add( linearSeries );
        outputChart.getData().add( gSeries );
        outputChart.getData().add( bSeries );
        rSeries.setName( "R" );
        gSeries.setName( "G" );
        bSeries.setName( "B" );
        linearSeries.setName( "liniowy" );
    }

    private void handleCalculate( ActionEvent actionEvent ) {

        List<XYChart.Data<String, Number>> valuesR = calculateOutputLevels( inputLevels.get(), maxOutputLevel[R].get(), gamma[R].doubleValue() );
        List<XYChart.Data<String, Number>> valuesG = calculateOutputLevels( inputLevels.get(), maxOutputLevel[G].get(), gamma[G].doubleValue() );
        List<XYChart.Data<String, Number>> valuesB = calculateOutputLevels( inputLevels.get(), maxOutputLevel[B].get(), gamma[B].doubleValue() );

        StringBuilder sb = new StringBuilder();
        sb.append( String.format( "// inLevels=%d, gammaR=%s, gammaB=%s, gammaG=%s, maxR=%s, maxG=%s, maxB=%s\n", inputLevels.get(), gamma[R].get(), gamma[G].get(), gamma[B].get(), maxOutputLevel[R].get(), maxOutputLevel[G].get(), maxOutputLevel[B].get() ) );
        sb.append( printArduinoCode( "gammaR", valuesR ) );
        sb.append( printArduinoCode( "gammaG", valuesG ) );
        sb.append( printArduinoCode( "gammaB", valuesB ) );
        dataTableTA.setText( sb.toString() );

        outputChart.getYAxis().setAutoRanging( false );
        outputChart.getYAxis().setAnimated( false );
        outputChart.getXAxis().setAnimated( false );
        outputChart.getXAxis().setLabel( "Poziom wejściowy" );
        outputChart.setAnimated( false );
        outputChart.setLegendVisible( false );
        NumberAxis yAxis = ( NumberAxis ) outputChart.getYAxis();
        yAxis.setLowerBound( 0.0 );
        int totalMaxOutputLevel = Stream.of( maxOutputLevel ).map( IntegerProperty::get ).max( Integer::compare ).orElse( 255 );
        yAxis.setUpperBound( totalMaxOutputLevel );
        rSeries.getData().setAll( valuesR );
        gSeries.getData().setAll( valuesG );
        bSeries.getData().setAll( valuesB );
        linearSeries.getData().setAll( calculateOutputLevels( inputLevels.get(), totalMaxOutputLevel, 1.0 ) );

    }

    private List<XYChart.Data<String, Number>> calculateOutputLevels( final int inLevels, final int maxOutLevel, final double gammaValue ) {
        List<XYChart.Data<String, Number>> result = new ArrayList<>();
        for ( int i = 0; i < ( inLevels + 1 ); i++ ) {
            result.add( new XYChart.Data<>( String.valueOf( i ), ( int ) ( Math.pow( ( float ) i / ( float ) inLevels, gammaValue ) * maxOutLevel + 0.5 ) ) );
        }
        return result;
    }

    private String printArduinoCode( String varName, List<XYChart.Data<String, Number>> values ) {
        StringBuilder sb = new StringBuilder();
        sb.append( String.format( "const uint8_t %s[] = {", varName ) );
        for ( int i = 0; i < ( values.size() ); i++ ) {
            if ( i > 0 ) {
                sb.append( ',' );
            }
            if ( ( i & 15 ) == 0 ) {
                sb.append( "\n  " );
            }
            sb.append( String.format( "%3d", values.get( i ).getYValue().intValue() ) );
        }
        sb.append( "\n};\n" );
        return sb.toString();
    }
}

package com.roszczyk.ledcalculator

import com.roszczyk.ledcalculator.controller.NewController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(*args)
}

class Main : Application() {

    override fun start(primaryStage: Stage) {
        val resource = Main::class.java.classLoader.getResourceAsStream("calculator.fxml")
        val loader = FXMLLoader()
        loader.setController(NewController())

        val root = loader.load<Parent>(resource)

        primaryStage.title = "Kalkulator krzywej Gamma dla LED"
        primaryStage.scene = Scene(root, 900.0, 650.0)
        primaryStage.show()
    }

}

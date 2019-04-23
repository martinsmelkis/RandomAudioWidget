package org.ecosia.randomaudiowidget.model.usecase

// Created by martinsmelkis on 03/04/2019.
abstract class AbstractUseCase {

    /**
     * This method contains the actual business logic of the usecase.
     * It could instead call the execute() method of an usecase to make sure the operation is done on a background thread.
     * This method could only be called directly while doing unit/integration tests.
     */
    protected abstract fun run()

    /**
     * Cleanup fields that might leak, if any.
     */
    protected abstract fun destroy()
}

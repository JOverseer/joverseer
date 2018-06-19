package org.joverseer.ui;

/**
 * Lifecycle events
 * 
 * @author Marios Skounakis
 */
public enum LifecycleEventsEnum {
    GameChangedEvent,
    SelectedHexChangedEvent,
    SelectedTurnChangedEvent,
    OrderChangedEvent,
    RefreshOrders,
    RefreshMapItems,
    EconomyCalculatorUpdate,
    EditOrderEvent,
    MapMetadataChangedEvent,
    RefreshHexItems,
    ListviewTableAutoresizeModeToggle,
    ListviewRefreshItems,
    RefreshTurnMapItems,
    SelectCharEvent,
    SendOrdersByChat,
    NoteUpdated,
    SetPalantirMapStyleEvent,
    ZoomIncreaseEvent,
    ZoomDecreaseEvent,
    GameLoadedEvent
}

//интерфейс для управления состоянием окон
package robots.src.gui;

import java.util.Map;

public interface Savable {
    void saveState(Map<String, String> state);
    void restoreState(Map<String, String> state);
}
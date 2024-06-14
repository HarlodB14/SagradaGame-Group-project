package Controller;

import DAL.PublicObjectiveCardDAO;
import Model.*;

import java.sql.SQLException;
import java.util.*;

public class ObjectiveCardController {

    private PublicObjectiveCardDAO publicObjectiveCardDAO;

    public ObjectiveCardController(){
        this.publicObjectiveCardDAO = new PublicObjectiveCardDAO();
    }

    public int checkObjectives(Player player, ArrayList<PlayerFrameField> playerFrameFields, Game game) throws SQLException {
        int score1 = checkPublicObjectives(playerFrameFields, game);
        int score2 = checkPrivateObjectives(player, playerFrameFields);

        return score1 + score2;
    }

    private int checkPublicObjectives(ArrayList<PlayerFrameField> playerFrameFields, Game game) throws SQLException {
        int score = 0;

        List<Public_ObjectiveCard> publicObjectiveCards = game.getPublicObjectiveCards();

        for (Public_ObjectiveCard card : publicObjectiveCards) {
            int cardId = card.getIdPublicObjectiveCard();

            switch (cardId) {
                case 1:
                    score += checkPublicObjectivecard_ShadeVariety(playerFrameFields);
                    break;
                case 2:
                    score += checkPublicObjectivecard_MediumShades(playerFrameFields);
                    break;
                case 3:
                    score += checkPublicObjectivecard_ColumnShadeVariety(playerFrameFields);
                    break;
                case 4:
                    score += checkPublicObjectivecard_ColumnColorVariety(playerFrameFields);
                    break;
                case 5:
                    score += checkPublicObjectivecard_DarkShades(playerFrameFields);
                    break;
                case 6:
                    score += checkPublicObjectivecard_ColorVariety(playerFrameFields);
                    break;
                case 7:
                    score += checkPublicObjectivecard_RowColorVariety(playerFrameFields);
                    break;
                case 8:
                    score += checkPublicObjectivecard_Diagonals(playerFrameFields);
                    break;
                case 9:
                    score += checkPublicObjectivecard_LightShades(playerFrameFields);
                    break;
                case 10:
                    score += checkPublicObjectivecard_RowShadeVariety(playerFrameFields);
                    break;
                default:
                    //System.out.println("Invalid objective card ID.");
                    break;
            }
        }
        return score;
    }


public int checkPrivateObjectives(Player player, List<PlayerFrameField> playerFrameFields) {
    int score = 0;
    String privateObjectiveColor = player.getPrivateObjectiveCardColor();

    if (playerFrameFields != null && privateObjectiveColor != null) {
        long count = playerFrameFields.stream()
                .filter(field -> field.getDieColor() != null && field.getDieColor().equals(privateObjectiveColor))
                .count();

        score = (int) count;
    }

    return score;
}

    private int checkPublicObjectivecard_ShadeVariety(ArrayList<PlayerFrameField> playerFrameFields) throws SQLException {
        Map<Integer, Integer> shadeCounts = new HashMap<>();

        for (PlayerFrameField playerFrameField : playerFrameFields) {
            int shadeValue = playerFrameField.getDieNumber();
            shadeCounts.put(shadeValue, shadeCounts.getOrDefault(shadeValue, 0) + 1);
        }

        boolean hasVariety = shadeCounts.values().stream().allMatch(count -> count >= 1);
        int setCount = (int) shadeCounts.values().stream().filter(count -> count >= 1).count();
        return hasVariety ? (setCount * 5) : 0;
    }

    private int checkPublicObjectivecard_MediumShades(ArrayList<PlayerFrameField> playerFrameFields) {
        int score;
        int countThree = 0;
        int countFour = 0;

        for (PlayerFrameField field : playerFrameFields) {
            int eyes = field.getDieNumber();
            if (eyes == 3) {
                countThree++;
            } else if (eyes == 4) {
                countFour++;
            }
        }

        int sets = Math.min(countThree, countFour);
        score = sets * 2;

        return score;
    }

    private int checkPublicObjectivecard_ColumnShadeVariety(ArrayList<PlayerFrameField> playerFrameFields) throws SQLException {
        int score = 0;
        int columns = 5;

        for (int column = 1; column <= columns; column++) {
            Set<Integer> shades = new HashSet<>();

            for (PlayerFrameField field : playerFrameFields) {
                if (field.getPositionX() == column) {
                    int shade = field.getDieNumber();
                    shades.add(shade);
                }
            }

            if (shades.size() == 4) {
                score += 4;
            }
        }

        return score;
    }

    private int checkPublicObjectivecard_ColumnColorVariety(ArrayList<PlayerFrameField> playerFrameFields) {
        int score = 0;
        int columns = 5;

        for (int column = 1; column <= columns; column++) {
            Set<String> colors = new HashSet<>();

            for (PlayerFrameField field : playerFrameFields) {
                if (field.getPositionX() == column) {
                    String color = field.getDieColor();
                    colors.add(color);
                }
            }

            if (colors.size() == 4) {
                score += 5;
            }
        }

        return score;
    }

    private int checkPublicObjectivecard_DarkShades(ArrayList<PlayerFrameField> playerFrameFields) {
        int score;
        int countFive = 0;
        int countSix = 0;

        for (PlayerFrameField field : playerFrameFields) {
            int eyes = field.getDieNumber();
            if (eyes == 5) {
                countFive++;
            } else if (eyes == 6) {
                countSix++;
            }
        }

        int sets = Math.min(countFive, countSix);
        score = sets * 2;

        return score;
    }

    private int checkPublicObjectivecard_ColorVariety(ArrayList<PlayerFrameField> playerFrameFields) {
        Map<String, Integer> colorCounts = new HashMap<>();

        for (PlayerFrameField playerFrameField : playerFrameFields) {
            String colorValue = playerFrameField.getDieColor();
            colorCounts.put(colorValue, colorCounts.getOrDefault(colorValue, 0) + 1);
        }

        boolean hasVariety = colorCounts.values().stream().allMatch(count -> count >= 1);
        int setCount = (int) colorCounts.values().stream().filter(count -> count >= 1).count();
        return hasVariety ? (setCount * 5) : 0;
    }

    private int checkPublicObjectivecard_RowColorVariety(ArrayList<PlayerFrameField> playerFrameFields) {
        int score = 0;
        int rows = 4;

        for (int row = 1; row <= rows; row++) {
            Set<String> colors = new HashSet<>();

            for (PlayerFrameField field : playerFrameFields) {
                if (field.getPositionY() == row) {
                    String color = field.getDieColor();
                    colors.add(color);
                }
            }

            if (colors.size() == 5) {
                score += 6;
            }
        }

        return score;
    }

    private int checkPublicObjectivecard_Diagonals(ArrayList<PlayerFrameField> playerFrameFields) {
        int score = 0;

        for (PlayerFrameField field : playerFrameFields) {
            String color = field.getDieColor();
            int x = field.getPositionX();
            int y = field.getPositionY();

            if (x < 5 && y < 4 &&
                    (isSameColor(playerFrameFields, x + 1, y + 1, color) ||
                            isSameColor(playerFrameFields, x - 1, y + 1, color))) {
                score++;
            }
        }

        return score;
    }

private boolean isSameColor(List<PlayerFrameField> playerFrameFields, int x, int y, String color) {
    if (playerFrameFields != null && color != null) {
        for (PlayerFrameField field : playerFrameFields) {
            if (field != null && field.getPositionX() == x && field.getPositionY() == y && color.equals(field.getDieColor())) {
                return true;
            }
        }
    }
    return false;
}

    private int checkPublicObjectivecard_LightShades(ArrayList<PlayerFrameField> playerFrameFields) throws SQLException {
        int countOne = 0;
        int countTwo = 0;

        for (PlayerFrameField field : playerFrameFields) {
            int eyes = field.getDieNumber();
            if (eyes == 1) {
                countOne++;
            } else if (eyes == 2) {
                countTwo++;
            }
        }

        int sets = Math.min(countOne, countTwo);
        return sets * 2;
    }

    private int checkPublicObjectivecard_RowShadeVariety(ArrayList<PlayerFrameField> playerFrameFields) throws SQLException {
        int rows = 4;
        int score = 0;

        for (int row = 1; row <= rows; row++) {
            Set<Integer> shades = new HashSet<>();

            for (PlayerFrameField field : playerFrameFields) {
                if (field.getPositionY() == row) {
                    int shade = field.getDieNumber();
                    shades.add(shade);
                }
            }

            if (shades.size() == 5) {
                score += 5;
            }
        }

        return score;
    }
}

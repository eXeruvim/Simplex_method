
public class Lang{
	
	public static String langOption = "russian";
	
	public static String getLangString(String origString){
		if(langOption=="russian"){
			return origString;
		} else if(langOption=="english"){
			switch(origString){
				//MainApp-strings
				case "Рассчитать":
					return "Calculate";
				case "Показать только результат":
					return "Show result only";
				case "Показать подробное решение":
					return "Show intermediate steps";
				case "Решить самому":
					return "Choose pivot manually";
				case "Simplex-method":
					return "Simplex Calculator";
				case "Целевая функция:":
					return "Objective function:";
				case "Ограничения:":
					return "Constraints:";
				case "Завершение по окончании 1.000.000 итераций":
					return "Terminating calculation after 1,000,000 iterations";
				case "\nШаг. ":
					return "\nStep ";
				case "Неизвестный код ошибки.\n(Код: ":
					return "The program received a number in an unknown format. \n(Error code: ";
				case "Выход за пределы сводной таблицы":
					return "The pivot function received a row index exceeding number of rows in the Simplex tableau";
				case "Выход за пределы симплекс-таблицы":
					return "The pivot function received a column index exceeding the number of columns in the Simplex tableau";
				case "Деление на 0. Выберите другой индекс":
					return "The chosen pivot element will cause division by 0. Please choose another element.";
				case "Программа попыталась разделить на 0.":
					return "The program attempted division by 0. The program will be terminated.";
				case "Функция не ограничена. Оптимальное решение отсутствует.":
					return "The problem is consistent but unbounded.";
				case "Нет решений.":
					return "The problem is inconsistent - no solutions exist.";
				case "Задача решена.":
					return "The problem has been solved.";
				case "Вариант 1 (b \u2265 0)":
					return "Applying case 1 (b \u2265 0)";
				case "Вариант 2 (b_i \u2264 0)":
					return "Applying case 2 (b_i \u2264 0)";
				case "Вариант 1 для задачи на минимум (-c \u2265 0)":
					return "Applying case 1 for the minimum problem (-c \u2265 0)";
				case "caseToString(): неизвестный номер кода (":
					return "The caseToString()-function received an unknown case number (";
				
				case "Решение: \n":
					return "The problem is consistent and has the solution:\n";
				case "Все остальные независимые переменные равны 0.":
					return "All other independent variables are 0";
				case "Оптимальное значение: ":
					return "The optimal value is ";
				case "Программа не смогла вывести решение, потому что printSolution () получила неизвестный код (":
					return "The program could not print the solution because printSolution() received an unknown case number (";
				case "\nПотребовалось ":
					return "\nThe solution was found after ";
				case " шагов.":
					return " steps.";
					
				//PopupWindows
				case "Количество переменных:":
					return "Number of variables:";
				case "Количество ограничений:":
					return "Number of constraints";
				case "Задайте размер ":
					return "Enter the dimensions of the problem";
				case "Введите только положительные целые числа":
					return "Use positive integers only";
				case "Количество переменных должно быть положительным целым числом!":
					return "The number of independent variables must be a positive integer!";
				case "Количество ограничений должно быть положительным целым числом!":
					return "The number of constraints must be a positive integer!";
				case "Введите индекс строки и столбца.\nМаксимальные значения(":
					return "Enter a row and column index.\nThe maximum values are (";
				case "Строка:":
					return "Row:";
				case "Столбец:":
					return "Column:";
				case "Выберите точку поворота":
					return "Choose pivot";
				case "Выбранный элемент выходит за пределы симплекс-таблицы":
					return "The chosen pivot exceeds the dimensions of the simplex tableau";
				case "Выйти из программы":
					return "Exit program";
				case "Произошла ошибка!":
					return "An error has occurred";
				default:
					PopupWindows.ErrorMessage("The string "+origString+" has not been translated!", false);
					return origString;
			}
		}
		// Опции языка не заданы
		PopupWindows.ErrorMessage("Program received an unknown language option.", true);
		return origString;	//Obsolete return
	}
}

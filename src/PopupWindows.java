import javax.swing.*;


public class PopupWindows{
	
	public static boolean initiate(){
		JTextField varField = new JTextField(2);
		JTextField constraintField = new JTextField(2);
		
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel(Lang.getLangString("Количество переменных:")));
		inputPanel.add(varField);
		inputPanel.add(Box.createHorizontalStrut(4));
		inputPanel.add(new JLabel(Lang.getLangString("Количество ограничений:")));
		inputPanel.add(constraintField);
		
		// Параметры
		Object[] options={"OK"};
		
		int result = JOptionPane.showOptionDialog(null,inputPanel,Lang.getLangString("Введите размер"), JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		
		if(result==JOptionPane.OK_OPTION){ // Если все норм
			
			int var=0;
			int constraint=0;
			try{
				var = Integer.parseInt(varField.getText());
				constraint = Integer.parseInt(constraintField.getText());
			} catch(NumberFormatException e){
				ErrorMessage(Lang.getLangString("Введите только положительные целые числа"),false);
				return false;
			}
			
			if(var>0){
				MainApp.numOfIndependentVar = var;
			} else {
				ErrorMessage(Lang.getLangString("Количество переменных должно быть целым положительным числом!"),false);
				return false;
			}
			if(constraint>0){
				MainApp.numOfConstraints = constraint;
			} else {
				ErrorMessage(Lang.getLangString("Количество ограничений должно быть положительным числом!"),false);
				return false;
			}
		} else {
			//	Если нажат X
			//	ДОБАВИТЬ keylistener escape key!!!!!!!!!!!!!!!!!
			// FIXME: 020, 20.09.2021
			System.exit(0);
		}
		
		return true;
	}

	// Начало алгоритма
	public static int[] pivotPromt(int maxRow, int maxColumn){
		int[] res = new int[2];
		
		JTextField rowField = new JTextField(2);
		JTextField columnField = new JTextField(2);
		
		JTextArea infoMessage = new JTextArea();
		infoMessage.setEditable(false);
		infoMessage.setOpaque(false);
		infoMessage.setText(Lang.getLangString("Введите индекс строки и столбца.\nМаксимальные значения (")
							+ maxRow + ", " + maxColumn + ").");
		
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel(Lang.getLangString("Ряд:")));
		inputPanel.add(rowField);
		inputPanel.add(Box.createHorizontalStrut(4));
		inputPanel.add(new JLabel(Lang.getLangString("Столбец:")));
		inputPanel.add(columnField);
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new BoxLayout(framePanel,BoxLayout.Y_AXIS));
		framePanel.add(infoMessage);
		framePanel.add(inputPanel);
		
		// Параметры
		Object[] options={"OK"};
		
		boolean feasiblePivot=false;
		
		while(!feasiblePivot){
			int result = JOptionPane.showOptionDialog(null,framePanel,Lang.getLangString("Выберите элемент поворота"), JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			if(result==JOptionPane.OK_OPTION){ // Если ок

				int row=0;
				int column=0;
				try{
					row = Integer.parseInt(rowField.getText())-1;
					column = Integer.parseInt(columnField.getText())-1;
				} catch(NumberFormatException e){
					ErrorMessage(Lang.getLangString("Введите только положительные целые числа!"),false);
					continue;
				}

				if(row>=0 && row<maxRow){
					res[0] = row;
				} else {
					ErrorMessage(Lang.getLangString("Выход за пределы таблицы"),false);
					continue;
				}
				if(column>=0 && column<maxColumn){
					res[1] = column;
				} else {
					ErrorMessage(Lang.getLangString("Выход за пределы таблицы"),false);
					continue;
				}
			} else {
				return null;
			}
			feasiblePivot=true;
		}
		return res;
	}
	
	
	public static void ErrorMessage(String message, boolean endProgram){
		if(endProgram){
			// Параметры
			Object[] options={Lang.getLangString("Выйти из программы")};
			
			JOptionPane.showOptionDialog(null, message, Lang.getLangString("Произошла ошибка"), JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			System.exit(0);
		} else {
			// Параметры
			Object[] options={"OK"};
			
			JOptionPane.showOptionDialog(null, message, Lang.getLangString("Произошла ошибка"), JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		}
	}
}
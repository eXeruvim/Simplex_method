import javax.swing.*;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.*;

import java.util.ArrayList;
import org.apache.commons.math3.fraction.BigFraction;

public class MainApp extends JFrame{

	private static final long serialVersionUID = 1L;
	
	//	Аргументы, используемые в программе
	public static int numOfIndependentVar=0;
	public static int numOfConstraints=0;
	public static boolean isMaximizing=true;
	private static final int textFieldSize=3;
	static boolean limitSteps=true;
	
	private String varName="x";
	
	private static String[] dropMinMax={"MAX","MIN"};
	static JComboBox<String> dropDown = new JComboBox<String>(dropMinMax);
	
	//	Unicode для знаков:
	//	2264 - <=,
	//	2265 - >=
	private String[] dropEquals={""+'\u2264',"=",""+'\u2265'};
	
	static JComboBox<String> constraintEq[] = new JComboBox[numOfConstraints];
	
	ArrayList<JLabel> labels = new ArrayList<JLabel>();
	
	//	Кнопки
	JButton buttonUdregn = new JButton(Lang.getLangString("Решить"));
	JRadioButton resultOnly = new JRadioButton(Lang.getLangString("Показать только результат"));
	JRadioButton calculateAll = new JRadioButton(Lang.getLangString("Показать промежуточный результат"));
	JRadioButton manualPivot = new JRadioButton(Lang.getLangString("Выбрать точку поворота вручную"));
	
	
	//	Ввод целевой функции
	static JTextField inputObjectFunc[] = new JTextField[numOfIndependentVar];
	
	//	Матрица условий (ограничения / переменные)
	static JTextField inputConstraintMatrix[][] = new JTextField[numOfConstraints][numOfIndependentVar];
	static JTextField inputConstraintVec[] = new JTextField[numOfConstraints];
	
	//	Симплекс-таблица
	static String topVector[]= new String[0];
	static String leftVector[]=new String[0];
	static BigFraction constraintMatrix[][]=new BigFraction[0][0];
	static BigFraction constraintVec[]= new BigFraction[0];
	static BigFraction objectFunc[]= new BigFraction[0];
	static BigFraction value= new BigFraction(0);
	
	static int pivotPoint[]={0,0};
	
	// Панель
	JPanel inputPanel = new JPanel();
		
		
	public static void main(String[] args){
			
		//	Проверка на Lang и -Nolimit
		for(int i=0;i<args.length;i++){
			if(args[i].toLowerCase().equals("-nolimit")){
				limitSteps=false;
				System.out.println("Запускает программу без ограничения количества шагов");
			} else if(args[i].toLowerCase().equals("-lang=english")){
				Lang.langOption="english";
			}
		}

		//Спрашиваем пользователя о размере матрицы
		while(!PopupWindows.initiate()){
		}
			
		new MainApp(200+75*numOfIndependentVar,200+40*numOfConstraints);

	}
		
	public MainApp(int width,int height){
		//	Установить размер и положение
		this.setSize(width,height);
		this.setLocationRelativeTo(null);
		
		//	При закрытии окна
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//	Заголовок
		this.setTitle(Lang.getLangString("Симплекс-метод"));
		
		//	listener для кнопок
		ActionListener listen = new ListenForButton();

		FocusListener listenFocus = new ListenForFocus();
		
		//	Расположение на панеле ввода
		inputPanel.setLayout(new GridBagLayout());
				
		GridBagConstraints theLayout = new GridBagConstraints();
		
		theLayout.gridx=1;
		theLayout.gridy=1;
		theLayout.gridwidth=1;
		theLayout.gridheight=1;
		theLayout.weightx=50;
		theLayout.weighty=10;
		theLayout.insets= new Insets(5,5,5,5);
		theLayout.anchor = GridBagConstraints.CENTER;
		theLayout.fill = GridBagConstraints.HORIZONTAL;
		
		
		//	Добавление элементов на панель
		theLayout.gridwidth=20;
		inputPanel.add(new JLabel(Lang.getLangString("Целевая фукнция:")),theLayout);
		theLayout.gridwidth=2;
		
		theLayout.gridy=2;
		inputPanel.add(dropDown,theLayout);
		theLayout.gridwidth=1;
		theLayout.gridx+=1;
		
		//	Массив для целевой функции
		inputObjectFunc = new JTextField[numOfIndependentVar];
		for(int i=0;i<inputObjectFunc.length;i++){
			inputObjectFunc[i]= new JTextField("0");
			inputObjectFunc[i].setColumns(textFieldSize);
			inputObjectFunc[i].setHorizontalAlignment(JTextField.CENTER);
			inputObjectFunc[i].addFocusListener(listenFocus);
		}
		//	Добавление функции на панель
		for(int i=0;i<inputObjectFunc.length;i++){
			theLayout.gridx+=1;
			inputPanel.add(inputObjectFunc[i],theLayout);
			theLayout.gridx+=1;
			if(i!=inputObjectFunc.length-1){
				labels.add(new JLabel(varName + String.valueOf(i+1) + " +"));
			} else {
				labels.add(new JLabel(varName + String.valueOf(i+1)));		
			}
			inputPanel.add(labels.get(labels.size()-1),theLayout);
		}
		
		
		// Создание расположения филдов для ограничений
		// Создание раскрывающихся списков для равенства / неравенства
		inputConstraintVec = new JTextField[numOfConstraints];
		constraintEq = new JComboBox[numOfConstraints];
		dropDown.addActionListener(listen);
		for(int i=0;i<inputConstraintVec.length;i++){
			inputConstraintVec[i]= new JTextField("0");
			inputConstraintVec[i].setColumns(textFieldSize);
			inputConstraintVec[i].setHorizontalAlignment(JTextField.CENTER);
			inputConstraintVec[i].addFocusListener(listenFocus);
			constraintEq[i]= new JComboBox(dropEquals);
		}
		//	Матрица ограничений
		inputConstraintMatrix =  new JTextField[numOfConstraints][numOfIndependentVar];
		for(int i=0;i<numOfConstraints;i++){
			for(int j=0;j<numOfIndependentVar;j++){
				inputConstraintMatrix[i][j] = new JTextField("0");
				inputConstraintMatrix[i][j].setColumns(textFieldSize);
				inputConstraintMatrix[i][j].setHorizontalAlignment(JTextField.CENTER);
				inputConstraintMatrix[i][j].addFocusListener(listenFocus);
			}
		}
		// FIXME: 020, 20.09.2021 
		//	Добавление "строк"
		theLayout.gridx=1;
		theLayout.gridy+=1;
		theLayout.gridwidth=20;
		inputPanel.add(new JLabel(Lang.getLangString("Условия и положения:")),theLayout); 
		theLayout.gridwidth=1;
		for(int i=0;i<inputConstraintVec.length;i++){
			theLayout.gridx=1;
			theLayout.gridy+=1;
			for(int j=0;j<numOfIndependentVar;j++){
				inputPanel.add(inputConstraintMatrix[i][j],theLayout);
				theLayout.gridx+=1;
				if(j!=numOfIndependentVar-1){
					labels.add(new JLabel(varName + String.valueOf(j+1)+ " +"));
				} else {
					labels.add(new JLabel(varName + String.valueOf(j+1)));
				}
				inputPanel.add(labels.get(labels.size()-1),theLayout);
				theLayout.gridx+=1;
			}
			// Раскрывающийся список и "строка"
			inputPanel.add(constraintEq[i],theLayout);
			theLayout.gridx+=1;
			inputPanel.add(inputConstraintVec[i],theLayout);
		}
		
		//	Добавление кнопок на панель
		theLayout.gridwidth=50;
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel,BoxLayout.X_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
			//	Добавление радиокнопок
			ButtonGroup operatingMode = new ButtonGroup();
			operatingMode.add(resultOnly);
			operatingMode.add(calculateAll);
			operatingMode.add(manualPivot);
			JPanel radioPanel = new JPanel();
			radioPanel.setLayout(new BoxLayout(radioPanel,BoxLayout.Y_AXIS));
			radioPanel.add(resultOnly);
			radioPanel.add(calculateAll);
			radioPanel.add(manualPivot);
			
		bottomPanel.add(radioPanel);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(buttonUdregn);
		calculateAll.setSelected(true);
		
		theLayout.gridy+=1;
		theLayout.gridx=1;
		inputPanel.add(new JSeparator(SwingConstants.HORIZONTAL),theLayout);
		theLayout.gridy+=1;
		inputPanel.add(bottomPanel,theLayout);
		buttonUdregn.addActionListener(listen);
		
		//	Добавление на фрейм панели + визуализация
		this.add(inputPanel);
		this.setVisible(true);
	}
	

	//	listener для кнопок
	private class ListenForButton implements ActionListener{

		 public void actionPerformed(ActionEvent e){
			 
			 if (e.getSource() == dropDown){
				 //	Смена условия неравенства
				 if(dropDown.getSelectedItem()=="MAX"){
					 varName="x";
					 for(int i=0;i<constraintEq.length;i++){
						 constraintEq[i].setSelectedItem(""+'\u2264');
					 }
				 } else {
					 varName="y";
					 for(int i=0;i<constraintEq.length;i++){
						 constraintEq[i].setSelectedItem(""+'\u2265');
					 }
					 
				 }
				 // Обновить все "лейблы"
				for(int i=0;i<labels.size();i++){
					if((i+1)%numOfIndependentVar!=0){
						labels.get(i).setText(varName + String.valueOf(i%numOfIndependentVar+1) + " +");
					} else {
						labels.get(i).setText(varName + String.valueOf(i%numOfIndependentVar+1));
					}
				}
			 } else if(e.getSource()== buttonUdregn){
				 convertToSimplex();
				 
				 OutputWindow out = new OutputWindow();
				 
				 out.outputFelt.append(printSimplex());
				 
				 if(manualPivot.isSelected()){
					 while(pivotPoint!=null){
						 // Сводный элемент
						 pivotPoint = PopupWindows.pivotPromt(constraintMatrix.length, constraintMatrix[0].length);
						 
						 if(pivotPoint!=null){
							 makePivot();
						 
							 out.outputFelt.append(printSimplex());
						 }
					 }
				 } else {
					 int caseNum=whichCase();
					 int stepNum=1;
					 while(caseNum>0){
						 if(stepNum==1000000 && limitSteps){
							 PopupWindows.ErrorMessage(Lang.getLangString("Лимит итераций (1кк)"),false);
							 return;
						 }
						 if(calculateAll.isSelected()){
							 out.outputFelt.append(Lang.getLangString("\nШаг №") + stepNum + "\n" + caseToString(caseNum) + "\tПоворот: " + (pivotPoint[0]+1) + "," + (pivotPoint[1]+1));
							 makePivot();

							 out.outputFelt.append(printSimplex());
						 } else {
							 makePivot();
						 }
						 stepNum+=1;
						 caseNum=whichCase();
					 }

					 //	Вывести результат
					 out.outputFelt.append(printSolution(caseNum, stepNum));
				 }
				 
			 }
		 }
	}
	
	// Реализация списков фокуса (focus-listener)
	private class ListenForFocus extends FocusAdapter{

		@Override
		public void focusGained(FocusEvent e) {
			// Выбрать все в поле
			// FIXME: 020, 20.09.2021 
			Component comp = e.getComponent();
			if(comp instanceof JTextField){
				((JTextField) comp).selectAll();
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			// FIXME: 020, 20.09.2021
			// Снять флажок 
			Component comp = e.getComponent();
			if(comp instanceof JTextField){
				((JTextField) comp).select(0, 0);
			}
		}
	}
	
	
	public static void convertToSimplex(){
		//	Проверка на доп ограничения
		for(int i=0;i<constraintEq.length;i++){
			if(constraintEq[i].getSelectedItem() == "="){
				numOfConstraints+=1;
			}
		}
		
		//	Сформировать матрицу в центре симплексной
		if(dropDown.getSelectedItem() == "MAX"){
			//	Если max, то нельзя транспонировать
			//	"строка" верха матрицы
			topVector = new String[numOfIndependentVar];
			for(int i=0;i<topVector.length;i++){
				topVector[i]="x"+String.valueOf(i+1);
			}
			
			//	"строка" левой стороны матрицы
			leftVector = new String[numOfConstraints];
			for(int i=0;i<leftVector.length;i++){
				leftVector[i]="y"+String.valueOf(i+1);
			}
			
			constraintMatrix = new BigFraction[numOfConstraints][numOfIndependentVar];
			constraintVec = new BigFraction[numOfConstraints];
			{int i=0;
			int origIndex=0;
			while(i<numOfConstraints){
				if(constraintEq[origIndex].getSelectedItem()== "="){
					for(int j=0;j<numOfIndependentVar;j++){
						//	Преобразование текста в число
						constraintMatrix[i][j]=stringToFrac(inputConstraintMatrix[origIndex][j].getText());
						constraintMatrix[i+1][j]=stringToFrac(inputConstraintMatrix[origIndex][j].getText()).multiply(BigFraction.MINUS_ONE);
					}
					
					//	Заполнение бинарной матрицы
					//	Преобразование текста в число
					constraintVec[i]=stringToFrac(inputConstraintVec[origIndex].getText());
					constraintVec[i+1]=stringToFrac(inputConstraintVec[origIndex].getText()).multiply(BigFraction.MINUS_ONE);
					
					i+=2;
					origIndex+=1;
				
				// Если выбран  >=
				} else if(constraintEq[origIndex].getSelectedItem()== ""+'\u2265'){
					for(int j=0;j<numOfIndependentVar;j++){
						//	Преобразование текста в число
						constraintMatrix[i][j]=stringToFrac(inputConstraintMatrix[origIndex][j].getText()).multiply(BigFraction.MINUS_ONE);
					}
					//	Заполнение бинарной матрицы
					//	Преобразование текста в число
					constraintVec[i]=stringToFrac(inputConstraintVec[origIndex].getText()).multiply(BigFraction.MINUS_ONE);					
					
					i+=1;
					origIndex+=1;
					
				} else {
				// Если знак по умолчанию
					for(int j=0;j<numOfIndependentVar;j++){
						//	Преобразование текста в число
						constraintMatrix[i][j]=stringToFrac(inputConstraintMatrix[origIndex][j].getText());
					}

					//	Заполнение бинарной матрицы
					//	Преобразование текста в число
					constraintVec[i]=stringToFrac(inputConstraintVec[origIndex].getText());
					
					i+=1;
					origIndex+=1;
				}
				
			}}	//	Блокировка для удаления int i
			
			//	Тогда для целевой функции
			objectFunc = new BigFraction[numOfIndependentVar];
			for(int i=0;i<inputObjectFunc.length;i++){
				//	Преобразовать в большие числа
				objectFunc[i]=stringToFrac(inputObjectFunc[i].getText()).multiply(BigFraction.MINUS_ONE);
			}

		} else {
			//	Симплекс-таблица для min
			// 	"линия" для верха таблицы
			topVector = new String[numOfConstraints];
			for(int i=0;i<topVector.length;i++){
				topVector[i]="x"+String.valueOf(i+1);
			}
			
			//	"линия" для левой части таблицы
			leftVector = new String[numOfIndependentVar];
			for(int i=0;i<leftVector.length;i++){
				leftVector[i]="y"+String.valueOf(i+1);
			}
			constraintMatrix = new BigFraction[numOfIndependentVar][numOfConstraints];
			objectFunc = new BigFraction[numOfConstraints];
			{int i=0;
			int origIndex=0;
			while(i<numOfConstraints){
				if(constraintEq[origIndex].getSelectedItem()== "="){
					for(int j=0;j<numOfIndependentVar;j++){
						//	текст в число
						constraintMatrix[j][i]=stringToFrac(inputConstraintMatrix[origIndex][j].getText());
						constraintMatrix[j][i+1]=stringToFrac(inputConstraintMatrix[origIndex][j].getText()).multiply(BigFraction.MINUS_ONE);
					}

					//	"линия" для целевой функции
					//	 текст в число
					objectFunc[i]=stringToFrac(inputConstraintVec[origIndex].getText()).multiply(BigFraction.MINUS_ONE);
					objectFunc[i+1]=stringToFrac(inputConstraintVec[origIndex].getText());

					i+=2;
					origIndex+=1;

					// Для =<
				} else if(constraintEq[origIndex].getSelectedItem()== ""+'\u2264'){
					for(int j=0;j<numOfIndependentVar;j++){
						//	текст в число
						constraintMatrix[j][i]=stringToFrac(inputConstraintMatrix[origIndex][j].getText()).multiply(BigFraction.MINUS_ONE);
					}
					//	"линия" для целевой функции
					//   текст в число
					objectFunc[i]=stringToFrac(inputConstraintVec[origIndex].getText()).multiply(BigFraction.MINUS_ONE);					

					i+=1;
					origIndex+=1;

				} else {
					// Состояние, которое нельзя отменять
					for(int j=0;j<numOfIndependentVar;j++){
						//	текст в число
						constraintMatrix[j][i]=stringToFrac(inputConstraintMatrix[origIndex][j].getText());
					}

					//	"линия" для целевой функции
					//	текст в число
					objectFunc[i]=stringToFrac(inputConstraintVec[origIndex].getText()).multiply(BigFraction.MINUS_ONE);

					i+=1;
					origIndex+=1;
				}

			}}	//	Блокировка для удаления int i

			//	Базис-матрица
			constraintVec = new BigFraction[numOfIndependentVar];
			for(int i=0;i<inputObjectFunc.length;i++){
				//	L�s input og omdan til tal
				constraintVec[i]=stringToFrac(inputObjectFunc[i].getText());
			}

			
			//конец для min
		}
	}
	
	
	// Функция для разделения строки на две (для дроби)
	public static String[] splitString(String input){
		String returnVal[] = {"",""};
		boolean hasFoundFrac=false;
		for(int i=0;i<input.length();i++){
			if(input.charAt(i)=='/'){
				hasFoundFrac=true;
			} else {
				if(hasFoundFrac){
					returnVal[1]+=input.charAt(i);
				} else {
					returnVal[0]+=input.charAt(i);
				}
			}
		}
		return returnVal;
	}
	
	
	
	//	Функция для преобразования строки в BigFraction
	static BigFraction stringToFrac(String input){
		BigFraction res = BigFraction.ZERO;
		try{
			if(input.contains("/")){
				String frac[] = splitString(input);
				res = new BigFraction(Integer.parseInt(frac[0]),Integer.parseInt(frac[1]));
			} else if(input.contains(".")){
				res = new BigFraction(Double.parseDouble(input));
			} else {
				res = new BigFraction(Integer.parseInt(input));
			}
		} catch (NumberFormatException e){
			PopupWindows.ErrorMessage(Lang.getLangString("Неизвестный числовой формат.\n(Код: ") + e + ")",true);
		}
		return res;
	}
	
	
	
	//	Функция для отрисовки simplex-таблицы
	String printSimplex(){
		String res="\n\t";
		//	Отрисовка верхней линии
		for(int i=0;i<topVector.length;i++){
			res+=topVector[i] + "\t";
		}
		res+="\n";
		//	Отрисовка остальных компонентов (левая линия + матрица)
		for(int i=0;i<leftVector.length;i++){
			res+=leftVector[i] +"\t";
			for(int j=0;j<constraintMatrix[i].length;j++){
				res+=constraintMatrix[i][j].toString() + "\t";
			}
			res+=constraintVec[i].toString() + "\t";
			res+="\n";
		}
		//	Нижняя строка
		res+="\t";
		for(int i=0;i<objectFunc.length;i++){
			res+=objectFunc[i].toString() + "\t";
		}
		res+=value.toString() + "\n";
		return res;
	}
	
	
	//	Функция для выбора решения с-м
	int whichCase(){
		boolean bPositive=true;
		boolean cPositive=true;
		
		for(int i=0;i<constraintVec.length;i++){
			if(constraintVec[i].compareTo(BigFraction.ZERO)==-1){
				bPositive=false;
				break;
			}
		}
		for(int i=0;i<objectFunc.length;i++){
			if(objectFunc[i].compareTo(BigFraction.ZERO)==-1){
				cPositive=false;
				break;
			}
		}
		
		
		if(bPositive && cPositive){
			return 0;
			// Задача решена
		} else if(bPositive){
			//	Поиск элемента для поворота
			// FIXME: 020, 20.09.2021 (576, 580)
			for(int i=0;i<objectFunc.length;i++){
				if(objectFunc[i].compareTo(BigFraction.ZERO)==-1){
					// Выбор начальную точку
					pivotPoint[1]=i;
					break;
				}
			}
			//	Поиск минимального положительного элемента
			boolean existsPositive=false;
			BigFraction smallestVal= new BigFraction(1000000000);
			for(int i=0;i<constraintMatrix.length;i++){
				if(constraintMatrix[i][pivotPoint[1]].compareTo(BigFraction.ZERO)==1){
					existsPositive=true;
					//	Если bi / aij меньше, то изменить
					if(constraintVec[i].divide(constraintMatrix[i][pivotPoint[1]]).compareTo(smallestVal)==-1){
						pivotPoint[0]=i;
						smallestVal=constraintVec[i].divide(constraintMatrix[i][pivotPoint[1]]);
					}
				}
			}
			//	Если мин. положительного элемента нет
			if(!existsPositive){
				return -2;
			} else {
				return 1;
			}
			
			
			
		} else if (cPositive){
			//	Поиск минимального базиса
			for(int i=0;i<constraintVec.length;i++){
				if(constraintVec[i].compareTo(BigFraction.ZERO)==-1){
					// Разрешающий элемент
					pivotPoint[0]=i;
					break;
				}
			}
			//	Есть ли в строке отрицательный элемент
			//	Поиск  b/ aij ближайший к 0
			boolean existsNegative=false;
			BigFraction smallestVal= new BigFraction(1000000000);
			for(int j=0;j<constraintMatrix[0].length;j++){
				if(constraintMatrix[pivotPoint[0]][j].compareTo(BigFraction.ZERO)==-1){
					existsNegative=true;
					//	Поиск и замена минимального значения
					if(objectFunc[j].divide(constraintMatrix[pivotPoint[0]][j]).abs().compareTo(smallestVal)==-1){
						pivotPoint[1]=j;
						smallestVal=objectFunc[j].divide(constraintMatrix[pivotPoint[0]][j]);
					}
				}
			}
			//	Если отрицательный элемент не найден, то всё ок типа
			if(!existsNegative){
				return -1;
			} else {
				return 3;
			}
		
			
			
		} else {
			int firstNegative=0;
			//	Поиск первого отрицательного элемента  в базисе
			for(int i=0;i<constraintVec.length;i++){
				if(constraintVec[i].compareTo(BigFraction.ZERO)==-1){
					// Разрешающий элемент
					firstNegative=i;
					pivotPoint[0]=i;
					break;
				}
			}
			//	Проверка на присутствие меньшего отрицательного элемента
			//  И выбор его в качестве минимального
			boolean existsNegative=false;
			BigFraction smallestVal= new BigFraction(1000000000);
			for(int j=0;j<constraintMatrix[firstNegative].length;j++){
				if(constraintMatrix[firstNegative][j].compareTo(BigFraction.ZERO)==-1){
					existsNegative=true;
					pivotPoint[1]=j;
					smallestVal=constraintVec[firstNegative].divide(constraintMatrix[firstNegative][j]);
					break;
				}
			}
			//	Если таких нет, то ко-не-ц
			if(!existsNegative){
				return -1;
			}
			
			//	Если есть, то поиск элемента, который bi/aij наименьший
			// 	А bi >= 0 и aij >= 0
			// 	То выбрать в качестве разрешающего элемента
			for(int i=0;i<constraintMatrix.length;i++){
				//	Заменить значение
				if(constraintMatrix[i][pivotPoint[1]].compareTo(BigFraction.ZERO)==1 && constraintVec[i].compareTo(BigFraction.ZERO)!=-1//newline
						&& constraintVec[i].divide(constraintMatrix[i][pivotPoint[1]]).compareTo(smallestVal)==-1){
					smallestVal=constraintVec[i].divide(constraintMatrix[i][pivotPoint[1]]);
					pivotPoint[0]=i;
				}
			}
			
			return 2;
			
		}
	}
	
	//	Сводная функция
	void makePivot(){
		if(pivotPoint[0]>=constraintMatrix.length){
			PopupWindows.ErrorMessage(Lang.getLangString("Индекс выходит за пределы симплекс-таблицы"),true);
		}
		if(pivotPoint[1]>=constraintMatrix[0].length){
			PopupWindows.ErrorMessage(Lang.getLangString("Индекс выходит за пределы симплекс-таблицы"),true);
		}
		
		BigFraction pivotVal = constraintMatrix[pivotPoint[0]][pivotPoint[1]];
		
		//	Проверка деления на 0
		if(pivotVal.equals(BigFraction.ZERO)){
			if(manualPivot.isSelected()){
				PopupWindows.ErrorMessage(Lang.getLangString("Выбранный элемент вызывает деление на 0. Выберите другой элемент"), false);
				return;
			} else {
				PopupWindows.ErrorMessage(Lang.getLangString("Попытка деления на 0."), true);
			}	
		}
		
		//	Изменить переменные
		String temp = topVector[pivotPoint[1]];
		topVector[pivotPoint[1]]=leftVector[pivotPoint[0]];
		leftVector[pivotPoint[0]]=temp;

		// Вычисление значений в разных строках и столбцах
		// В сводном(ой) столбце / строке
		value=value.subtract((constraintVec[pivotPoint[0]].multiply(objectFunc[pivotPoint[1]])).divide(pivotVal));
		
		//	Вычисление b
		for(int i=0;i<constraintVec.length;i++){
			if(i!=pivotPoint[0]){
				constraintVec[i]=constraintVec[i].subtract((constraintVec[pivotPoint[0]].multiply(constraintMatrix[i][pivotPoint[1]])).divide(pivotVal));
			}
		}
		constraintVec[pivotPoint[0]]=constraintVec[pivotPoint[0]].divide(pivotVal);
		
		//	Вычисление целевой функции
		for(int j=0;j<objectFunc.length;j++){
			if(j!=pivotPoint[1]){
				objectFunc[j]=objectFunc[j].subtract((constraintMatrix[pivotPoint[0]][j].multiply(objectFunc[pivotPoint[1]])).divide(pivotVal));
			}
		}
		objectFunc[pivotPoint[1]]=(objectFunc[pivotPoint[1]].divide(pivotVal)).multiply(BigFraction.MINUS_ONE);
		
		
		//	Рассчет матрицы
		for(int i=0;i<constraintMatrix.length;i++){
			for(int j=0;j<constraintMatrix[i].length;j++){
				if(i!=pivotPoint[0] && j!=pivotPoint[1]){
					constraintMatrix[i][j]=constraintMatrix[i][j].subtract((constraintMatrix[pivotPoint[0]][j].multiply(constraintMatrix[i][pivotPoint[1]])).divide(pivotVal));
				}
			}
		}
		
		//	Рассчет сводного столбца
		for(int i=0;i<constraintMatrix.length;i++){
			if(i!=pivotPoint[0]){
				constraintMatrix[i][pivotPoint[1]]= (constraintMatrix[i][pivotPoint[1]].divide(pivotVal)).multiply(BigFraction.MINUS_ONE);
			}
		}
		
		//	Рассчет сводной строки
		for(int j=0;j<constraintMatrix[0].length;j++){
			if(j!=pivotPoint[1]){
				constraintMatrix[pivotPoint[0]][j]= constraintMatrix[pivotPoint[0]][j].divide(pivotVal);
			}
		}
		
		//	Рассчет разрешающего элемента
		constraintMatrix[pivotPoint[0]][pivotPoint[1]]= BigFraction.ONE.divide(pivotVal);
		
	}
	
	
	//	Функция для преобразования кода в строку
	String caseToString(int caseNum){
		switch(caseNum){
		
			case -2:
				return Lang.getLangString("Функция не ограничена. Оптимальное решение отсутствует.");
				
			case -1:
				return Lang.getLangString("Решений нет.");
				
			case 0:
				return Lang.getLangString("Решено.");
				
			case 1:
				return Lang.getLangString("Код 1 (b \u2265 0)");
				
			case 2:
				return Lang.getLangString("Код 2 (b_i \u2264 0)");
				
			case 3:
				return Lang.getLangString("Код 1 для задачи с минимизацией (-c \u2265 0)");
				
			default:
				PopupWindows.ErrorMessage(Lang.getLangString("caseToString(): получено неизвестный код (") + caseNum + ")",true);
				return "";
		}
	}
	
	//	Функция для отчета об ошибках
	String printSolution(int caseNum, int numOfSteps){
		String res="\n\n";
		if(caseNum==0 && dropDown.getSelectedItem() == "MAX"){
			res+=Lang.getLangString("Имеет решение:\n");
			for(int i=0;i<leftVector.length;i++){
				if(leftVector[i].contains("x")){
					res+="\t" + leftVector[i] + " = " + constraintVec[i].toString() + "\n";
				}
			}
			res+="\n" + Lang.getLangString("Все переменные равны 0.");
			res+="\n" + Lang.getLangString("Оптимальное решение ") + value;
		} else if(caseNum==0){
				res+=Lang.getLangString("Имеет решение:\n");
			for(int i=0;i<topVector.length;i++){
				if(topVector[i].contains("y")){
					res+="\t" + topVector[i] + " = " + objectFunc[i].toString() + "\n";
				}
			}
			res+="\n" + Lang.getLangString("Все переменные равны 0.");
			res+="\n" + Lang.getLangString("Оптимальное решение ") + value;
		} else if(caseNum==-1){
			res+=Lang.getLangString("Решения нет.");
		} else if(caseNum==-2){
			res+=Lang.getLangString("Функция не ограничена. Оптимальное решение отсутствует.");
		} else {
			PopupWindows.ErrorMessage(Lang.getLangString("Программа не смогла вывести решение, потому что printSolution () получила неизвестный код. (") + caseNum + ")", false);
		}
		res+=Lang.getLangString("\nПотребовалось шагов: ") + numOfSteps;
		return res;
	}
}

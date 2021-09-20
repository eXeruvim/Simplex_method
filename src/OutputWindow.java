import javax.swing.*;


public class OutputWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	
	public JTextArea outputFelt = new JTextArea(20,40);
	public JScrollPane panel = new JScrollPane(outputFelt);
	
	public OutputWindow(){
		
		//	Установить размер и положение
		this.setSize(500,350);
		this.setLocationRelativeTo(null);
		
		//	При закрытии
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//	Заголовок
		this.setTitle(Lang.getLangString("Симплекс-метод"));
		
		this.add(panel);
		this.setVisible(true);
	}
	
}
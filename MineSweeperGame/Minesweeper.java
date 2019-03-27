import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Minesweeper extends JPanel implements ActionListener, MouseListener {

	JFrame frame;
	JMenuBar menu;
	JMenu game, theme, controls;
	JMenuItem beg, inter, expert;
	JMenuItem[] images;
	JMenuItem defaults, emojis, spongebob;
	int currentTheme;

	JPanel panel, upper, scoreBoard;
	JToggleButton[][] togglers;
	int width = 9;
	int height = 9;
	ImageIcon[] flag, mine, middlePic;
	JButton centerButton;
	JLabel timerTime, flagsLeft;
	int startingFlag, numFlags;
	HashSet<Point> minePos;
	boolean initial = true;
	boolean gameOver = false;
	Timer timer;
	int time;
	int[][] arrPos;
	String stringg = "     ";

	public Minesweeper() {
		frame = new JFrame("Minesweeper");
		frame.add(this);
		frame.setSize(40 * width, 40 * height);

		mine = new ImageIcon[4];
		flag = new ImageIcon[4];
		middlePic = new ImageIcon[5];
		mine[0] = new ImageIcon("mine.png");
		flag[0] = new ImageIcon("flag.png");
		middlePic[0] = new ImageIcon("smiley.png");
		mine[1] = new ImageIcon("devil.png");
		flag[1] = new ImageIcon("goodEmoji.png");
		middlePic[1] = new ImageIcon("emojiSmiley.png");
		mine[2] = new ImageIcon("plankton.png");
		flag[2] = new ImageIcon("spongebob.png");
		middlePic[2] = new ImageIcon("patrick.png");
		mine[3] = new ImageIcon(mine[0].getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		flag[3] = new ImageIcon(flag[0].getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		middlePic[3] = new ImageIcon(middlePic[0].getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
		middlePic[4] = new ImageIcon("win.png");;


		menu = new JMenuBar();
		game = new JMenu("Game");
		theme = new JMenu("Icons");
		controls = new JMenu("Control");
		beg = new JMenuItem("Beginner");
		inter = new JMenuItem("Intermediate");
		expert = new JMenuItem("Expert");
		beg.addActionListener(this);
		inter.addActionListener(this);
		expert.addActionListener(this);
		game.add(beg);
		game.add(inter);
		game.add(expert);

		currentTheme = 0;
		defaults = new JMenuItem("Default");
		emojis = new JMenuItem("Emojis");
		spongebob = new JMenuItem("Spongebob");
		images = new JMenuItem[3];
		images[0] = defaults;
		images[1] = emojis;
		images[2] = spongebob;
		for(int i=0;i<images.length;i++) {
			images[i].addActionListener(this);
			theme.add(images[i]);
		}
		JLabel label1 = new JLabel(" Left-click an empty square to reveal it. ");
		controls.add(label1);
		JLabel label2 = new JLabel(" Right-click an empty square to flag it. ");
		controls.add(label2);
		menu.add(game);
		menu.add(theme);
		menu.add(controls);
		togglers = new JToggleButton[width][height];
		arrPos = new int[width][height];
		panel = new JPanel();
		panel.setLayout(new GridLayout(togglers.length, togglers[0].length));
		for (int i = 0; i < togglers.length; i++) {
			for (int j = 0; j < togglers[0].length; j++) {
				togglers[i][j] = new JToggleButton();
				togglers[i][j].addMouseListener(this);
				panel.add(togglers[i][j]);
			}
		}
		minePos = new HashSet<>();

		scoreBoard = new JPanel(new FlowLayout(FlowLayout.CENTER));
		startingFlag = 10;
		flagsLeft = new JLabel(stringg + (startingFlag - numFlags) + stringg);
		centerButton = new JButton(middlePic[3]);
		centerButton.setPreferredSize(new Dimension(50, 50));
		centerButton.addActionListener(this);
		timerTime = new JLabel(stringg + time + stringg);
		scoreBoard.add(flagsLeft);
		scoreBoard.add(centerButton);
		scoreBoard.add(timerTime);

		upper = new JPanel(new BorderLayout());
		upper.add(menu, BorderLayout.NORTH);
		upper.add(scoreBoard, BorderLayout.SOUTH);

		frame.add(upper, BorderLayout.NORTH);
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == beg) {
			width = 9;
			height = 9;
			startingFlag = 10;
			switchLevel();
		}
		if (e.getSource() == inter) {
			width = 16;
			height = 16;
			startingFlag = 40;
			switchLevel();
		}
		if (e.getSource() == expert) {
			width = 30;
			height = 16;
			startingFlag = 99;
			switchLevel();
		}
		if (e.getSource() == defaults) {
			changeImage("default");
			switchLevel();
		}
		if (e.getSource() == emojis) {
			changeImage("emojis");
			switchLevel();
		}
		if (e.getSource() == spongebob) {
			changeImage("spongebob");
			switchLevel();
		}
		if (e.getSource() == centerButton) {
			switchLevel();
			middlePic[3] = new ImageIcon(middlePic[currentTheme].getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
			centerButton.setIcon(middlePic[3]);
		}
		revalidate();
	}

	public void startGame(int roww, int coll) {
		ArrayList<Point> cantHave = new ArrayList<Point>();
		for (int x = roww-1; x < roww+2; x++) {
				for (int y = coll-1; y < coll+2; y++) {
					cantHave.add(new Point(x, y));
				}
		}
		minePos = new HashSet<>();
		int minesRemaining=0;
		if (width == 9)
			minesRemaining = 10;
		if (width == 16)
			minesRemaining = 40;
		if (width == 30)
			minesRemaining = 90;
		for (int i = 0; i < arrPos.length; i++) {
			for (int j = 0; j < arrPos[0].length; j++) {
				arrPos[i][j] = 0;
			}
		}
		while (minePos.size() != minesRemaining) {
			int row,column=0;
			do  {
				row = (int) (Math.random() * height);
				column = (int) (Math.random() * width);
			}while(togglers[row][column].getIcon() != null);
			Point newPoint = new Point(row,column);
			if (!cantHave.contains(newPoint)) {
				minePos.add(newPoint);
				arrPos[row][column] = -1;
			}
		}

		for (int x = 0; x < arrPos.length; x++) {
			for (int y = 0; y < arrPos[0].length; y++) {
				if (arrPos[x][y] == -1) {
					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							try {
								if (arrPos[x + i][y + j] > -1) {
									arrPos[x + i][y + j] += 1;
								}
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}
	}
	public void endGame() {
		timer.stop();
		for (int i = 0; i < togglers.length; i++) {
			for (int j = 0; j < togglers[0].length; j++) {
				togglers[i][j].setEnabled(false);
				if (minePos.contains(new Point(i, j))) {
					togglers[i][j].setIcon(mine[3]);
					togglers[i][j].setDisabledIcon(mine[3]);
				}
				gameOver = true;
			}
		}
	}
	public void expansion(int row, int col) {
		if (togglers[row][col].getIcon()==null) {
			togglers[row][col].setSelected(true);
			if (arrPos[row][col] > 0) {
				togglers[row][col].setMargin(new Insets(0, 0, 0, 0));
				togglers[row][col].setText(arrPos[row][col] + "");
			}
			else {
				for (int x = -1; x <=1; x++) {
					for (int y = -1; y <=1; y++) {
						try {
							if(!togglers[row + x][col + y].isSelected())
								expansion(row + x, col + y);
						} catch (Exception e) { }
					}
				}
			}
		}
	}
	public void changeImage(String selectedTheme) {
		if (selectedTheme.equals("default"))
			currentTheme = 0;
		else if (selectedTheme.equals("emojis"))
			currentTheme = 1;
		else if (selectedTheme.equals("spongebob"))
			currentTheme = 2;
		mine[3] = new ImageIcon(mine[currentTheme].getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		flag[3] = new ImageIcon(flag[currentTheme].getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		middlePic[3] = new ImageIcon(middlePic[currentTheme-1].getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
		centerButton.setIcon(middlePic[3]);
	}
	public void switchLevel() {
		frame.remove(panel);
		frame.setSize(40 * width, 40 * height);
		togglers = new JToggleButton[height][width];
		arrPos = new int[height][width];
		panel = new JPanel();
		panel.setLayout(new GridLayout(togglers.length, togglers[0].length));
		for (int i = 0; i < togglers.length; i++) {
			for (int j = 0; j < togglers[0].length; j++) {
				togglers[i][j] = new JToggleButton();
				togglers[i][j].addMouseListener(this);
				panel.add(togglers[i][j]);
			}
		}
		numFlags = 0;
		flagsLeft.setText(stringg + (startingFlag - numFlags) + stringg);
		gameOver = false;
		time = 0;
		if (!initial)
			timer.stop();
		initial = true;
		timerTime.setText(stringg + time + stringg);
		frame.add(panel, BorderLayout.CENTER);
	}
	public void won() {
		int emptySquares=0;
		int howManyFlags=0;
		for (int i = 0; i < togglers.length; i++) {
			for (int j = 0; j < togglers[0].length; j++) {
				if (!togglers[i][j].isSelected()) {
					if (minePos.contains(new Point(i, j))) {
						howManyFlags++;
					}
				}
				else {
					emptySquares++;
				}
			}
		}
		int empty = width*height-startingFlag;
		if (emptySquares == empty && howManyFlags == startingFlag) {
			gameOver = true;
			middlePic[3] = new ImageIcon(middlePic[4].getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));
			centerButton.setIcon(middlePic[3]);
			endGame();
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		if (!gameOver) {
			if (initial) {
				int milli = 1000;
				ActionListener actionListener = new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						time++;
						timerTime.setText(stringg + time + stringg);
					}
				};
				timer = new Timer(milli,actionListener);
				timer.start();
				for (int i = 0; i < togglers.length; i++) {
					for (int j = 0; j < togglers[0].length; j++) {
						if (e.getSource() == togglers[i][j]) {
							startGame(i, j);
						}
					}
				}
				initial = false;
			}
			if (e.getButton() == MouseEvent.BUTTON1) {
				for (int i = 0; i < togglers.length; i++) {
					for (int j = 0; j < togglers[0].length; j++) {
						if (e.getSource() == togglers[i][j]) {
							togglers[i][j].setSelected(false);
							if (togglers[i][j].getIcon() == null)
							{
								if (minePos.contains(new Point(i, j)))
								{
									togglers[i][j].setIcon(mine[3]);
									togglers[i][j].setDisabledIcon(mine[3]);
									endGame();
								} else {
									if (togglers[i][j].isSelected() == false)
										expansion(i, j);
								}
							}
						}
					}
				}
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				for (int i = 0; i < togglers.length; i++) {
					for (int j = 0; j < togglers[0].length; j++) {
						if (e.getSource() == togglers[i][j] && togglers[i][j].isSelected() == false) {
							if (togglers[i][j].getIcon() == null) {
								togglers[i][j].setIcon(flag[3]);
								togglers[i][j].setDisabledIcon(flag[3]);
								numFlags++;
							} else {
								togglers[i][j].setIcon(null);
								numFlags--;
							}
							flagsLeft.setText(stringg + (startingFlag - numFlags) + stringg);
						}
					}
				}
			}
			if (numFlags==startingFlag) {
				won();
			}
		}
	}
	public static void main(String[] args) {
		Minesweeper game = new Minesweeper();
	}

}
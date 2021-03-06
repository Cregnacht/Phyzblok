package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class EntityPanel extends JPanel{


    private static final int PREF_W = 500;
    private static final int PREF_H = PREF_W;
    private ArrayList<PhyzRectangle> rectangles;

    public EntityPanel()
    {
        rectangles = new ArrayList<PhyzRectangle>();
        ActionMap actionMap = getActionMap();
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = getInputMap(condition);
        setSize(GameWindow.getInstance().getSize());
        setVisible(false);
    }


    public void AddShape(PhyzRectangle rectangle)
    {
        
        rectangles.add(rectangle);
    }


    public void AddShapes(List<PhyzRectangle> shapes)
    {
        for (PhyzRectangle sh : shapes)
            rectangles.add(sh);

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (PhyzRectangle rect : rectangles)
        {
            int[]  xPnts = {rect.x, rect.x, rect.x + rect.width, rect.x + rect.width},
                   yPnts = {rect.y, rect.y + rect.height, rect.y + rect.height, rect.y};

            Polygon p = new Polygon(xPnts, yPnts, 4);

            g2.setColor(rect.getEntity().getColour());
            //g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            //g2.draw(rect);
            g2.fill(p);
            g2.draw(p);

        }
    }


    //@Override
    //public void revalidate()
    //{
    //    super.revalidate();
    //    this.setSize(GameWindow.getInstance().getSize());
    //}


    public void updateBodies()
    {
        for (PhyzRectangle pr : rectangles)
            pr.doUpdate();
    }


    public void clearBodies()
    {
        rectangles = new ArrayList<PhyzRectangle>();
    }


    public ArrayList<PhyzRectangle> getEntities()
    {
        return rectangles;
    }
}

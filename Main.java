import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

class Main{
  public static void main(String[] args){
    new TOM();
  }
  static class TOM extends JFrame{
    PointerInfo pointerInfo;
    int w=100;
    Color numberColor=Color.red;
    Color dispColor=new Color(128,140,156);
    Color dispFrameColor=Color.cyan;
    Color thisColor=Color.orange;
    Color moseColor=Color.green;
    Color arrowColor=Color.white;//new Color(128,128,0);
    Font font=new Font(Font.MONOSPACED,Font.PLAIN,w*2/5);
    GraphicsEnvironment ge;
    Point grip=null;
    TOM(){
      ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
      setSize(w,w);
      setUndecorated(true);
      addMouseListener(
        new MouseAdapter(){
          public void mouseClicked(MouseEvent e){
            System.exit(0);
          }
          public void mousePressed(MouseEvent e){
            grip=e.getPoint();
          }
          public void mouseReleased(MouseEvent e){
            grip=null;
          }
        }
      );
      addMouseMotionListener(
        new MouseAdapter(){
          public void mouseDragged(MouseEvent e){
            if(grip!=null){
              int x=(int)(-grip.getX()+e.getXOnScreen());
              int y=(int)(-grip.getY()+e.getYOnScreen());
              setLocation(x,y);
            }
          }
        }
      );
      if(isAlwaysOnTopSupported()){
        setAlwaysOnTop(true);
      }
      setVisible(true);
      new Thread(){
        public void run(){
          while(!interrupted()){
            repaint();
            try{
              sleep(80);
            }catch(InterruptedException e){break;}
          }
        }
      }.start();
    }
    public void paint(Graphics gg){
      PointerInfo pi=MouseInfo.getPointerInfo();
      Point mp=pi.getLocation();
      if(pointerInfo!=null&&mp.equals(pointerInfo.getLocation())){
        return;
      }
      pointerInfo=pi;
      BufferedImage image=ge.getScreenDevices()[0].getDefaultConfiguration().createCompatibleImage(w,w);
      Graphics g=image.getGraphics();
      
      Rectangle rc=null;
      {//rectangle of display
        for(GraphicsDevice d:ge.getScreenDevices()){
          Rectangle rc2=d.getDefaultConfiguration().getBounds();
          rc=rc==null?rc2:rc.union(rc2);
        }
        g.setColor(dispColor);
        for(GraphicsDevice d:ge.getScreenDevices()){
          Rectangle rc2=d.getDefaultConfiguration().getBounds();
          g.fillRect(
            trans(rc2.getX()     ,rc.getX(),rc.getWidth() ,w),
            trans(rc2.getY()     ,rc.getY(),rc.getHeight(),w),
            trans(rc2.getWidth() ,0        ,rc.getWidth() ,w),
            trans(rc2.getHeight(),0        ,rc.getHeight(),w)
          );
        }
        g.setColor(dispFrameColor);
        for(GraphicsDevice d:ge.getScreenDevices()){
          Rectangle rc2=d.getDefaultConfiguration().getBounds();
          g.drawRect(
            trans(rc2.getX()     ,rc.getX(),rc.getWidth() ,w),
            trans(rc2.getY()     ,rc.getY(),rc.getHeight(),w),
            trans(rc2.getWidth() ,0        ,rc.getWidth() ,w),
            trans(rc2.getHeight(),0        ,rc.getHeight(),w)
          );
        }
      }
      {//num of disp
        g.setFont(font);
        g.setColor(numberColor);
        g.setFont(font);
        int di=0;
        GraphicsDevice[] gds=ge.getScreenDevices();
        GraphicsDevice gd=MouseInfo.getPointerInfo().getDevice();
        for(;di<gds.length;di++){
          if(gd.equals(gds[di])){
            break;
          }
        }
        g.drawString(di+"",w/2-w/2/5,w/2+w/5);
      }
      {//this window
        g.setColor(thisColor);
        g.fillRect(
          trans(getX()     ,rc.getX(),rc.getWidth() ,w),
          trans(getY()     ,rc.getY(),rc.getHeight(),w),
          trans(getWidth() ,0        ,rc.getWidth() ,w),
          trans(getHeight(),0        ,rc.getHeight(),w)
        );
      }
      {//cross of mouse
        g.setColor(moseColor);
        int tx=trans(mp.getX(),rc.getX(),rc.getWidth(),w);
        int ty=trans(mp.getY(),rc.getY(),rc.getHeight(),w);
        g.drawLine(tx+w/20,ty+w/20,tx-w/20,ty-w/20);
        g.drawLine(tx+w/20,ty-w/20,tx-w/20,ty+w/20);
        g.fillOval(tx-3,ty-3,6,6);
      }
      {//arrow
        g.setColor(arrowColor);
        {
          double dx=mp.getX()-getX()-w/2;
          double dy=mp.getY()-getY()-w/2;
          double theta=Math.atan2(dy,dx);
          double x=Math.cos(theta);
          double y=Math.sin(theta);
          java.awt.Polygon pl=new java.awt.Polygon(
            new int[]{(int)(w*(0.50*x+0.00*y+0.5)),(int)(w*( 0.40*x+0.05*y+0.5)),(int)(w*(0.40*x-0.05*y+0.5))},
            new int[]{(int)(w*(0.00*x+0.50*y+0.5)),(int)(w*(-0.05*x+0.40*y+0.5)),(int)(w*(0.05*x+0.40*y+0.5))},
            3
          );
          g.drawLine(
            (int)(w/4*x)+w/2,(int)(w/4*y+w/2),
            (int)(w/2*x)+w/2,(int)(w/2*y+w/2)
          );
          g.fillPolygon(pl);
        }
      }
      gg.drawImage(image,0,0,null);
    }
    static int trans(double v,double o,double bw, double tw){
      return(int)((v-o)*tw/bw);
    }
  }
}

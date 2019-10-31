
package edu.self.indy.indycloud.actionhandler;

public class CopyToPath extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String uri = walletAction.getParameter("uri").asText();
    String destPath = walletAction.getParameter("destPath").asText();

    // InputStream input = null;
    // BufferedOutputStream output = null;
    // try {

    //     input = reactContext.getContentResolver().openInputStream(Uri.parse(uri));
    //     output = new BufferedOutputStream(new FileOutputStream(destPath));

    //     {
    //         byte data[] = new byte[BUFFER];
    //         int count;
    //         while ((count = input.read(data, 0, BUFFER)) != -1) {
    //             output.write(data, 0, count);
    //         }
    //         input.close();
    //         output.close();
    //         promise.resolve(destPath);
    //     }
    // }

    // catch (IOException e) {
    //     promise.reject("copyToPathException", e.getMessage());
    //     e.printStackTrace();
    // }

    return destPath;
  }
}


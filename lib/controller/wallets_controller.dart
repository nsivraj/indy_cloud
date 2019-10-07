import 'package:aqueduct/aqueduct.dart';
import 'package:indy_cloud/indy_cloud.dart';

class WalletsController extends ResourceController {

  // @override
  // Future<RequestOrResponse> handle(Request request) async {
  //   return Response.ok('wallets');
  // }

  @Operation.get()
  Future<Response> getAllWallets() async {
    return Response.ok('');
  }

  @Operation.get('id')
  Future<Response> getWalletByID() async {
    final id = int.parse(request.path.variables['id']);
    final wallet = await findWalletById(id); //_heroes.firstWhere((hero) => hero['id'] == id, orElse: () => null);
    if (wallet == null) {
      return Response.notFound();
    }

    return Response.ok(id);
  }

  Future<String> findWalletById(int id) async {
    // TODO: see if the wallet with id really exists
    return id.toString();
  }
}
import 'package:aqueduct/aqueduct.dart';
import 'package:indy_cloud/indy_cloud.dart';
import 'package:indy_cloud/controller/wallet.dart';

class WalletsController extends ResourceController {

  // @override
  // Future<RequestOrResponse> handle(Request request) async {
  //   return Response.ok('wallets');
  // }

  // @Operation.get()
  // Future<Response> getAllWallets() async {
  //   return Response.ok('');
  // }

  @Operation.get('id')
  Future<Response> getWalletByID(@Bind.path('id') int id) async {
    final String walletId = await isWalletIdValid(id);
    if (walletId == null) {
      return Response.notFound();
    }

    return Response.ok(id);
  }

  @Operation.post('id', 'actionName', 'actionParams')
  Future<Response> operateOnWallet(
    @Bind.path('id') int id,
    @Bind.path('actionName') String actionName,
    @Bind.path('actionParams') String actionParams
  ) async {
    // final Wallet wallet = await findWalletById(id);
    // if (wallet == null) {
    //   return Response.notFound();
    // }

    return Response.ok(id);
  }

  Future<String> isWalletIdValid(int id) async {
    // TODO: see if the wallet with id really exists
    return id.toString();
  }

  Future<Wallet> findWalletById(int id) async {
    // TODO: find the wallet with id
    const Wallet wallet = null;
    return wallet;
  }

  bool isNullEmptyOrFalse(Object o) =>
    o == null || false == o || "" == o;
}
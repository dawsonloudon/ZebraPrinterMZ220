#import <Cordova/CDV.h>
#import "MfiBtPrinterConnection.h"

@interface ZebraPrinterMZ220 : CDVPlugin

@property(nonatomic,retain)NSMutableArray *bluetoothPrinters;
@property(nonatomic,retain)NSString *printerAddress;
@property(nonatomic,retain)id<ZebraPrinterConnection, NSObject> thePrinterConn;

- (void)findPrinters:(CDVInvokedUrlCommand*)command;
- (void)getDevices:(CDVInvokedUrlCommand*)command;
- (void)openConnection:(CDVInvokedUrlCommand*)command;
- (void)closeConnection:(CDVInvokedUrlCommand*)command;
- (void)print:(CDVInvokedUrlCommand*)command;

@end
